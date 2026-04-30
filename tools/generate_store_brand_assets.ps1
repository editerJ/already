param(
    [string]$OutputDir = "D:\앱 개발\마음정리 일기 출시 패키지\store-assets"
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

Add-Type -AssemblyName System.Drawing

function New-Color([string]$Hex, [int]$Alpha = 255) {
    $value = $Hex.TrimStart("#")
    $r = [Convert]::ToInt32($value.Substring(0, 2), 16)
    $g = [Convert]::ToInt32($value.Substring(2, 2), 16)
    $b = [Convert]::ToInt32($value.Substring(4, 2), 16)
    return [System.Drawing.Color]::FromArgb($Alpha, $r, $g, $b)
}

function Resolve-FontFamily([string[]]$Candidates) {
    $installed = [System.Drawing.Text.InstalledFontCollection]::new()
    foreach ($candidate in $Candidates) {
        foreach ($family in $installed.Families) {
            if ($family.Name -eq $candidate) {
                return $candidate
            }
        }
    }
    return "Arial"
}

function New-Font([string]$Family, [float]$Size, [string]$Style = "Regular") {
    return [System.Drawing.Font]::new($Family, $Size, [System.Drawing.FontStyle]::$Style, [System.Drawing.GraphicsUnit]::Pixel)
}

function New-RoundedPath([float]$X, [float]$Y, [float]$Width, [float]$Height, [float]$Radius) {
    $path = [System.Drawing.Drawing2D.GraphicsPath]::new()
    $d = $Radius * 2
    $path.AddArc($X, $Y, $d, $d, 180, 90)
    $path.AddArc($X + $Width - $d, $Y, $d, $d, 270, 90)
    $path.AddArc($X + $Width - $d, $Y + $Height - $d, $d, $d, 0, 90)
    $path.AddArc($X, $Y + $Height - $d, $d, $d, 90, 90)
    $path.CloseFigure()
    return $path
}

function Fill-RoundedRect($Graphics, $Brush, [float]$X, [float]$Y, [float]$Width, [float]$Height, [float]$Radius) {
    $path = New-RoundedPath $X $Y $Width $Height $Radius
    $Graphics.FillPath($Brush, $path)
    $path.Dispose()
}

function Draw-RoundedRect($Graphics, $Pen, [float]$X, [float]$Y, [float]$Width, [float]$Height, [float]$Radius) {
    $path = New-RoundedPath $X $Y $Width $Height $Radius
    $Graphics.DrawPath($Pen, $path)
    $path.Dispose()
}

function Draw-TextBlock($Graphics, [string]$Text, $Font, $Brush, [float]$X, [float]$Y, [float]$Width, [float]$Height, [string]$Align = "Near") {
    $format = [System.Drawing.StringFormat]::new()
    switch ($Align) {
        "Center" { $format.Alignment = [System.Drawing.StringAlignment]::Center }
        "Far" { $format.Alignment = [System.Drawing.StringAlignment]::Far }
        default { $format.Alignment = [System.Drawing.StringAlignment]::Near }
    }
    $format.LineAlignment = [System.Drawing.StringAlignment]::Near
    $rect = [System.Drawing.RectangleF]::new($X, $Y, $Width, $Height)
    $Graphics.DrawString($Text, $Font, $Brush, $rect, $format)
    $format.Dispose()
}

function Draw-Chip($Graphics, [float]$X, [float]$Y, [float]$Width, [float]$Height, [string]$Label, $FillColor, $TextColor, [bool]$Active) {
    $radius = $Height / 2
    $fillBrush = [System.Drawing.SolidBrush]::new($FillColor)
    $textBrush = [System.Drawing.SolidBrush]::new($TextColor)
    $borderColor = if ($Active) { $FillColor } else { New-Color "#DDE9E0" }
    $borderPen = [System.Drawing.Pen]::new($borderColor, 2)
    Fill-RoundedRect $Graphics $fillBrush $X $Y $Width $Height $radius
    Draw-RoundedRect $Graphics $borderPen $X $Y $Width $Height $radius
    Draw-TextBlock $Graphics $Label (New-Font $fontFamily 18 "Bold") $textBrush $X ($Y + 9) $Width 20 "Center"
    $fillBrush.Dispose()
    $textBrush.Dispose()
    $borderPen.Dispose()
}

function Draw-PhoneCard($Graphics, $Palette, [float]$X, [float]$Y) {
    $shadow = [System.Drawing.SolidBrush]::new([System.Drawing.Color]::FromArgb(22, 0, 0, 0))
    $bodyBrush = [System.Drawing.SolidBrush]::new($Palette.PhoneBody)
    $screenBrush = [System.Drawing.SolidBrush]::new($Palette.Screen)
    $accentBrush = [System.Drawing.SolidBrush]::new($Palette.Accent)
    $softBrush = [System.Drawing.SolidBrush]::new($Palette.AccentSoft)
    $surfaceBrush = [System.Drawing.SolidBrush]::new($Palette.Surface)
    $borderPen = [System.Drawing.Pen]::new($Palette.PhoneBorder, 3)

    Fill-RoundedRect $Graphics $shadow ($X + 12) ($Y + 18) 282 446 42
    Fill-RoundedRect $Graphics $bodyBrush $X $Y 282 446 42
    Fill-RoundedRect $Graphics $screenBrush ($X + 14) ($Y + 14) 254 418 32
    Draw-RoundedRect $Graphics $borderPen $X $Y 282 446 42

    $Graphics.FillRectangle($accentBrush, $X + 34, $Y + 44, 214, 40)
    Draw-TextBlock $Graphics "마음정리 일기" $Palette.MicroFont $Palette.WhiteBrush ($X + 42) ($Y + 54) 200 18 "Near"
    Draw-TextBlock $Graphics "후회를 기록하고 돌아보세요" $Palette.MicroBodyFont $Palette.SubBrush ($X + 34) ($Y + 98) 210 20 "Near"

    Fill-RoundedRect $Graphics $surfaceBrush ($X + 34) ($Y + 132) 214 92 18
    Draw-TextBlock $Graphics "무슨 일이 있었나요?" $Palette.MicroBodyFont $Palette.TextBrush ($X + 48) ($Y + 146) 160 18 "Near"
    Draw-TextBlock $Graphics "회의에서 급하게 말해..." $Palette.MicroBodyFont $Palette.SubBrush ($X + 48) ($Y + 172) 150 18 "Near"
    Fill-RoundedRect $Graphics $surfaceBrush ($X + 34) ($Y + 238) 214 60 18
    Draw-TextBlock $Graphics "어떤 감정이 남았나요?" $Palette.MicroBodyFont $Palette.TextBrush ($X + 48) ($Y + 252) 170 18 "Near"
    Draw-TextBlock $Graphics "미안함, 후회" $Palette.MicroBodyFont $Palette.SubBrush ($X + 48) ($Y + 274) 120 18 "Near"
    Fill-RoundedRect $Graphics $accentBrush ($X + 34) ($Y + 314) 214 42 18
    Draw-TextBlock $Graphics "저장하고 AI 분석 받기" $Palette.MicroFont $Palette.WhiteBrush ($X + 34) ($Y + 325) 214 18 "Center"

    Fill-RoundedRect $Graphics $softBrush ($X + 34) ($Y + 374) 214 34 16
    Draw-TextBlock $Graphics "최신순 정렬 · 최근 기록 12개" $Palette.MicroBodyFont $Palette.AccentBrush ($X + 48) ($Y + 383) 170 18 "Near"

    $shadow.Dispose()
    $bodyBrush.Dispose()
    $screenBrush.Dispose()
    $accentBrush.Dispose()
    $softBrush.Dispose()
    $surfaceBrush.Dispose()
    $borderPen.Dispose()
}

function New-Canvas([int]$Width, [int]$Height) {
    $bitmap = [System.Drawing.Bitmap]::new($Width, $Height)
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    $graphics.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias
    $graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $graphics.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
    $graphics.TextRenderingHint = [System.Drawing.Text.TextRenderingHint]::ClearTypeGridFit
    return @{
        Bitmap = $bitmap
        Graphics = $graphics
    }
}

function Save-Canvas($Canvas, [string]$Path) {
    $Canvas.Bitmap.Save($Path, [System.Drawing.Imaging.ImageFormat]::Png)
    $Canvas.Graphics.Dispose()
    $Canvas.Bitmap.Dispose()
}

New-Item -ItemType Directory -Path $OutputDir -Force | Out-Null

$fontFamily = Resolve-FontFamily @("Helvetica", "Arial", "Segoe UI", "Tahoma")

$palette = [ordered]@{
    Background = New-Color "#F4FBF6"
    BackgroundDeep = New-Color "#E0F1E4"
    Accent = New-Color "#5E9470"
    AccentDark = New-Color "#35684A"
    AccentSoft = New-Color "#D7EBDD"
    Surface = New-Color "#FFFFFF"
    SurfaceAlt = New-Color "#EEF6F0"
    Text = New-Color "#1E2A22"
    SubText = New-Color "#5D6F63"
    WhiteBrush = [System.Drawing.SolidBrush]::new([System.Drawing.Color]::White)
    TextBrush = [System.Drawing.SolidBrush]::new((New-Color "#1E2A22"))
    SubBrush = [System.Drawing.SolidBrush]::new((New-Color "#5D6F63"))
    AccentBrush = [System.Drawing.SolidBrush]::new((New-Color "#5E9470"))
    AccentDarkBrush = [System.Drawing.SolidBrush]::new((New-Color "#35684A"))
    BackgroundBrush = [System.Drawing.SolidBrush]::new((New-Color "#F4FBF6"))
    BackgroundDeepBrush = [System.Drawing.SolidBrush]::new((New-Color "#E0F1E4"))
    AccentSoftBrush = [System.Drawing.SolidBrush]::new((New-Color "#D7EBDD"))
    SurfaceBrush = [System.Drawing.SolidBrush]::new((New-Color "#FFFFFF"))
    PhoneBody = New-Color "#F9FFFA"
    PhoneBorder = New-Color "#D8E7DB"
    Screen = New-Color "#F5FBF7"
    TitleFont = New-Font $fontFamily 48 "Bold"
    SubtitleFont = New-Font $fontFamily 30 "Regular"
    BadgeFont = New-Font $fontFamily 22 "Bold"
    TaglineFont = New-Font $fontFamily 28 "Bold"
    BodyFont = New-Font $fontFamily 20 "Regular"
    IconFont = New-Font $fontFamily 148 "Bold"
    MarkFont = New-Font $fontFamily 62 "Bold"
    MicroFont = New-Font $fontFamily 12 "Bold"
    MicroBodyFont = New-Font $fontFamily 11 "Regular"
}

try {
    $iconCanvas = New-Canvas 512 512
    $g = $iconCanvas.Graphics
    $g.Clear($palette.Background)

    $glowBrush = [System.Drawing.SolidBrush]::new([System.Drawing.Color]::FromArgb(48, 94, 148, 112))
    $circleBrush = [System.Drawing.SolidBrush]::new($palette.Accent)
    $circlePen = [System.Drawing.Pen]::new($palette.AccentDark, 6)
    $detailPen = [System.Drawing.Pen]::new([System.Drawing.Color]::FromArgb(32, 53, 104, 74), 4)

    $g.FillEllipse($glowBrush, 56, 56, 400, 400)
    $g.FillEllipse($circleBrush, 92, 92, 328, 328)
    $g.DrawEllipse($circlePen, 92, 92, 328, 328)
    $g.DrawArc($detailPen, 120, 120, 272, 272, 28, 58)
    $g.DrawString("J", $palette.IconFont, $palette.WhiteBrush, 174, 142)

    $iconPath = Join-Path $OutputDir "app-icon-512.png"
    Save-Canvas $iconCanvas $iconPath

    $glowBrush.Dispose()
    $circleBrush.Dispose()
    $circlePen.Dispose()
    $detailPen.Dispose()

    $featureCanvas = New-Canvas 1024 500
    $g2 = $featureCanvas.Graphics
    $g2.Clear($palette.Background)

    $leftGradient = [System.Drawing.Drawing2D.LinearGradientBrush]::new(
        [System.Drawing.RectangleF]::new(0, 0, 1024, 500),
        $palette.Background,
        $palette.BackgroundDeep,
        0
    )
    $g2.FillRectangle($leftGradient, 0, 0, 1024, 500)

    $shapeBrush = [System.Drawing.SolidBrush]::new([System.Drawing.Color]::FromArgb(70, 215, 235, 221))
    $shapeBrush2 = [System.Drawing.SolidBrush]::new([System.Drawing.Color]::FromArgb(42, 94, 148, 112))
    $g2.FillEllipse($shapeBrush, -40, -70, 340, 340)
    $g2.FillEllipse($shapeBrush2, 640, -90, 420, 420)
    $g2.FillEllipse($shapeBrush, 720, 300, 220, 220)

    Fill-RoundedRect $g2 $palette.AccentDarkBrush 66 72 168 56 22
    Draw-TextBlock $g2 "MIND JOURNAL" $palette.BadgeFont $palette.WhiteBrush 66 88 168 20 "Center"
    Draw-TextBlock $g2 "마음정리 일기" $palette.TitleFont $palette.TextBrush 66 156 360 64 "Near"
    Draw-TextBlock $g2 "Capture regret. Reflect clearly. Choose a better next action." $palette.SubtitleFont $palette.TextBrush 66 226 430 80 "Near"
    Draw-TextBlock $g2 "Simple private journaling with AI reflection, archive, insights, and app lock." $palette.BodyFont $palette.SubBrush 66 328 380 72 "Near"

    Draw-Chip $g2 66 410 118 38 "AI 회고" $palette.AccentSoft $palette.Accent $true
    Draw-Chip $g2 198 410 132 38 "보관함" $palette.Surface $palette.SubText $false
    Draw-Chip $g2 344 410 120 38 "잠금" $palette.Surface $palette.SubText $false

    Draw-PhoneCard $g2 $palette 648 28

    $markShadow = [System.Drawing.SolidBrush]::new([System.Drawing.Color]::FromArgb(36, 0, 0, 0))
    $markCircle = [System.Drawing.SolidBrush]::new($palette.Accent)
    $g2.FillEllipse($markShadow, 536, 346, 120, 120)
    $g2.FillEllipse($markCircle, 520, 330, 120, 120)
    Draw-TextBlock $g2 "J" $palette.MarkFont $palette.WhiteBrush 520 354 120 52 "Center"

    $featurePath = Join-Path $OutputDir "feature-graphic-1024x500.png"
    Save-Canvas $featureCanvas $featurePath

    $leftGradient.Dispose()
    $shapeBrush.Dispose()
    $shapeBrush2.Dispose()
    $markShadow.Dispose()
    $markCircle.Dispose()
}
finally {
    foreach ($key in @(
        "WhiteBrush", "TextBrush", "SubBrush", "AccentBrush", "AccentDarkBrush",
        "BackgroundBrush", "BackgroundDeepBrush", "AccentSoftBrush", "SurfaceBrush",
        "TitleFont", "SubtitleFont", "BadgeFont", "TaglineFont", "BodyFont", "IconFont",
        "MarkFont", "MicroFont", "MicroBodyFont"
    )) {
        if ($palette.Contains($key) -and $null -ne $palette[$key]) {
            $palette[$key].Dispose()
        }
    }
}
