param(
    [string]$OutputDir = "D:\앱 개발\마음정리 일기 출시 패키지\screenshots"
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

Add-Type -AssemblyName System.Drawing

function New-Color($hex, $alpha = 255) {
    $hexValue = $hex.TrimStart("#")
    $r = [Convert]::ToInt32($hexValue.Substring(0, 2), 16)
    $g = [Convert]::ToInt32($hexValue.Substring(2, 2), 16)
    $b = [Convert]::ToInt32($hexValue.Substring(4, 2), 16)
    return [System.Drawing.Color]::FromArgb($alpha, $r, $g, $b)
}

function New-Font($size, [string]$style = "Regular") {
    return [System.Drawing.Font]::new("Arial", [float]$size, [System.Drawing.FontStyle]::$style, [System.Drawing.GraphicsUnit]::Pixel)
}

function New-RoundedPath([float]$x, [float]$y, [float]$width, [float]$height, [float]$radius) {
    $path = [System.Drawing.Drawing2D.GraphicsPath]::new()
    $diameter = $radius * 2

    $path.AddArc($x, $y, $diameter, $diameter, 180, 90)
    $path.AddArc($x + $width - $diameter, $y, $diameter, $diameter, 270, 90)
    $path.AddArc($x + $width - $diameter, $y + $height - $diameter, $diameter, $diameter, 0, 90)
    $path.AddArc($x, $y + $height - $diameter, $diameter, $diameter, 90, 90)
    $path.CloseFigure()
    return $path
}

function Fill-RoundedRect($graphics, $brush, [float]$x, [float]$y, [float]$width, [float]$height, [float]$radius) {
    $path = New-RoundedPath $x $y $width $height $radius
    $graphics.FillPath($brush, $path)
    $path.Dispose()
}

function Draw-RoundedRect($graphics, $pen, [float]$x, [float]$y, [float]$width, [float]$height, [float]$radius) {
    $path = New-RoundedPath $x $y $width $height $radius
    $graphics.DrawPath($pen, $path)
    $path.Dispose()
}

function Draw-Card($graphics, [float]$x, [float]$y, [float]$width, [float]$height, [float]$radius, $fillColor, $borderColor) {
    $shadowBrush = [System.Drawing.SolidBrush]::new([System.Drawing.Color]::FromArgb(18, 0, 0, 0))
    $fillBrush = [System.Drawing.SolidBrush]::new($fillColor)
    $borderPen = [System.Drawing.Pen]::new($borderColor, 2)
    Fill-RoundedRect $graphics $shadowBrush ($x + 6) ($y + 10) $width $height $radius
    Fill-RoundedRect $graphics $fillBrush $x $y $width $height $radius
    Draw-RoundedRect $graphics $borderPen $x $y $width $height $radius
    $shadowBrush.Dispose()
    $fillBrush.Dispose()
    $borderPen.Dispose()
}

function Draw-TextBlock($graphics, [string]$text, $font, $brush, [float]$x, [float]$y, [float]$width, [float]$height, [string]$alignment = "Near") {
    $format = [System.Drawing.StringFormat]::new()
    switch ($alignment) {
        "Center" { $format.Alignment = [System.Drawing.StringAlignment]::Center }
        "Far" { $format.Alignment = [System.Drawing.StringAlignment]::Far }
        default { $format.Alignment = [System.Drawing.StringAlignment]::Near }
    }
    $format.LineAlignment = [System.Drawing.StringAlignment]::Near
    $rect = [System.Drawing.RectangleF]::new($x, $y, $width, $height)
    $graphics.DrawString($text, $font, $brush, $rect, $format)
    $format.Dispose()
}

function Draw-Chip($graphics, [float]$x, [float]$y, [float]$width, [float]$height, [string]$label, $fillColor, $textColor, [bool]$active) {
    $radius = $height / 2
    $fillBrush = [System.Drawing.SolidBrush]::new($fillColor)
    $textBrush = [System.Drawing.SolidBrush]::new($textColor)
    $penColor = if ($active) { $fillColor } else { New-Color "#E3D8C8" }
    $pen = [System.Drawing.Pen]::new($penColor, 2)
    Fill-RoundedRect $graphics $fillBrush $x $y $width $height $radius
    Draw-RoundedRect $graphics $pen $x $y $width $height $radius
    Draw-TextBlock $graphics $label (New-Font 21 "Bold") $textBrush $x ($y + 9) $width ($height - 8) "Center"
    $fillBrush.Dispose()
    $textBrush.Dispose()
    $pen.Dispose()
}

function Draw-Button($graphics, [float]$x, [float]$y, [float]$width, [float]$height, [string]$label, $fillColor) {
    $brush = [System.Drawing.SolidBrush]::new($fillColor)
    $white = [System.Drawing.SolidBrush]::new([System.Drawing.Color]::White)
    Fill-RoundedRect $graphics $brush $x $y $width $height 26
    Draw-TextBlock $graphics $label (New-Font 24 "Bold") $white $x ($y + 18) $width 40 "Center"
    $brush.Dispose()
    $white.Dispose()
}

function Draw-InputField($graphics, [float]$x, [float]$y, [float]$width, [float]$height, [string]$label, [string]$value, $palette) {
    Draw-TextBlock $graphics $label $palette.InputLabelFont $palette.TextBrush $x ($y - 6) $width 28 "Near"
    $fillBrush = [System.Drawing.SolidBrush]::new($palette.SurfaceCard)
    $pen = [System.Drawing.Pen]::new($palette.Border, 2)
    Fill-RoundedRect $graphics $fillBrush $x ($y + 30) $width $height 22
    Draw-RoundedRect $graphics $pen $x ($y + 30) $width $height 22
    Draw-TextBlock $graphics $value $palette.BodyFont $palette.SubBrush ($x + 18) ($y + 48) ($width - 36) ($height - 20) "Near"
    $fillBrush.Dispose()
    $pen.Dispose()
}

function Draw-StatusBar($graphics, $palette) {
    Draw-TextBlock $graphics "9:41" $palette.StatusFont $palette.TextBrush 58 28 120 24 "Near"
    $iconPen = [System.Drawing.Pen]::new($palette.Text, 4)
    $graphics.DrawLine($iconPen, 892, 41, 906, 41)
    $graphics.DrawLine($iconPen, 912, 37, 928, 37)
    $graphics.DrawLine($iconPen, 934, 33, 952, 33)
    $graphics.DrawArc($iconPen, 962, 22, 28, 28, 210, 120)
    $graphics.DrawRectangle($iconPen, 1000, 25, 40, 18)
    $graphics.FillRectangle($palette.TextBrush, 1003, 28, 29, 12)
    $graphics.FillRectangle($palette.TextBrush, 1042, 30, 4, 8)
    $iconPen.Dispose()
}

function Draw-AppHeader($graphics, $palette) {
    Draw-TextBlock $graphics "마음정리 일기" $palette.TitleFont $palette.TextBrush 58 94 500 44 "Near"
    Draw-TextBlock $graphics "후회를 기록해 다음 행동을 바꾸는 일기장" $palette.BodyFont $palette.SubBrush 58 142 650 30 "Near"
    Draw-Card $graphics 830 88 190 116 28 $palette.SurfaceCard $palette.Border
    Draw-TextBlock $graphics "7" $palette.CounterFont $palette.AccentBrush 830 110 190 40 "Center"
    Draw-TextBlock $graphics "days" $palette.SmallFont $palette.SubBrush 830 150 190 22 "Center"
}

function Draw-BottomNav($graphics, $palette, [string]$selected) {
    Draw-Card $graphics 34 1768 1012 118 34 $palette.SurfaceCard $palette.Border
    $items = @(
        @{ Key = "home"; Label = "홈"; X = 86 },
        @{ Key = "insights"; Label = "인사이트"; X = 316 },
        @{ Key = "archive"; Label = "보관함"; X = 560 },
        @{ Key = "settings"; Label = "설정"; X = 808 }
    )

    foreach ($item in $items) {
        $isSelected = $item.Key -eq $selected
        if ($isSelected) {
            $selectedBrush = [System.Drawing.SolidBrush]::new($palette.AccentSoft)
            Fill-RoundedRect $graphics $selectedBrush ($item.X - 32) 1784 178 84 28
            $selectedBrush.Dispose()
        }
        $textBrush = if ($isSelected) { $palette.AccentBrush } else { $palette.SubBrush }
        Draw-TextBlock $graphics $item.Label $palette.SmallFont $textBrush $item.X 1832 120 24 "Center"

        $iconColor = if ($isSelected) { $palette.Accent } else { $palette.SubText }
        $iconPen = [System.Drawing.Pen]::new($iconColor, 3)
        switch ($item.Key) {
            "home" {
                $graphics.DrawRectangle($iconPen, $item.X + 34, 1798, 30, 24)
                $graphics.DrawLine($iconPen, $item.X + 31, 1803, $item.X + 49, 1788)
                $graphics.DrawLine($iconPen, $item.X + 67, 1803, $item.X + 49, 1788)
            }
            "insights" {
                $graphics.DrawLine($iconPen, $item.X + 30, 1818, $item.X + 30, 1796)
                $graphics.DrawLine($iconPen, $item.X + 46, 1818, $item.X + 46, 1804)
                $graphics.DrawLine($iconPen, $item.X + 62, 1818, $item.X + 62, 1790)
            }
            "archive" {
                $graphics.DrawRectangle($iconPen, $item.X + 30, 1794, 36, 28)
                $graphics.DrawLine($iconPen, $item.X + 30, 1804, $item.X + 66, 1804)
                $graphics.DrawLine($iconPen, $item.X + 38, 1788, $item.X + 38, 1798)
                $graphics.DrawLine($iconPen, $item.X + 58, 1788, $item.X + 58, 1798)
            }
            "settings" {
                $graphics.DrawEllipse($iconPen, $item.X + 34, 1794, 28, 28)
                $graphics.DrawEllipse($iconPen, $item.X + 42, 1802, 12, 12)
            }
        }
        $iconPen.Dispose()
    }
}

function New-Palette {
    $palette = [ordered]@{}
    $palette.Background = New-Color "#F6F1E8"
    $palette.SurfaceCard = New-Color "#FFFDFC"
    $palette.SurfaceMuted = New-Color "#EFE6D8"
    $palette.Accent = New-Color "#1E5C4F"
    $palette.AccentSoft = New-Color "#D8E7E0"
    $palette.Text = New-Color "#1F2321"
    $palette.SubText = New-Color "#66706A"
    $palette.Border = New-Color "#E3D8C8"
    $palette.Quote = New-Color "#F2DFD7"
    $palette.Premium = New-Color "#7A4A35"
    $palette.TextBrush = [System.Drawing.SolidBrush]::new($palette.Text)
    $palette.SubBrush = [System.Drawing.SolidBrush]::new($palette.SubText)
    $palette.AccentBrush = [System.Drawing.SolidBrush]::new($palette.Accent)
    $palette.WhiteBrush = [System.Drawing.SolidBrush]::new([System.Drawing.Color]::White)
    $palette.PremiumBrush = [System.Drawing.SolidBrush]::new($palette.Premium)
    $palette.TitleFont = New-Font 38 "Bold"
    $palette.SectionFont = New-Font 34 "Bold"
    $palette.BodyFont = New-Font 22 "Regular"
    $palette.SmallFont = New-Font 20 "Regular"
    $palette.LabelFont = New-Font 17 "Bold"
    $palette.InputLabelFont = New-Font 20 "Bold"
    $palette.StatusFont = New-Font 18 "Bold"
    $palette.CounterFont = New-Font 30 "Bold"
    return $palette
}

function Dispose-Palette($palette) {
    foreach ($key in @("TextBrush", "SubBrush", "AccentBrush", "WhiteBrush", "PremiumBrush", "TitleFont", "SectionFont", "BodyFont", "SmallFont", "LabelFont", "InputLabelFont", "StatusFont", "CounterFont")) {
        $value = $palette[$key]
        if ($null -ne $value) {
            $value.Dispose()
        }
    }
}

function New-Canvas($filePath) {
    $bitmap = [System.Drawing.Bitmap]::new(1080, 1920)
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    $graphics.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias
    $graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $graphics.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
    $graphics.TextRenderingHint = [System.Drawing.Text.TextRenderingHint]::ClearTypeGridFit
    return @{
        Bitmap = $bitmap
        Graphics = $graphics
        Path = $filePath
    }
}

function Save-Canvas($canvas) {
    $canvas.Bitmap.Save($canvas.Path, [System.Drawing.Imaging.ImageFormat]::Png)
    $canvas.Graphics.Dispose()
    $canvas.Bitmap.Dispose()
}

function Draw-EntryCard($graphics, $palette, [float]$x, [float]$y, [string]$tag, [string]$time, [string]$title, [string]$situation, [string]$lesson, [string]$reflection) {
    Draw-Card $graphics $x $y 964 342 30 $palette.SurfaceCard $palette.Border
    Draw-Chip $graphics ($x + 28) ($y + 24) 110 42 $tag $palette.AccentSoft $palette.Accent $true
    Draw-TextBlock $graphics $time $palette.SmallFont $palette.SubBrush ($x + 770) ($y + 30) 160 20 "Far"
    Draw-TextBlock $graphics $title $palette.SectionFont $palette.TextBrush ($x + 28) ($y + 86) 680 42 "Near"
    Draw-TextBlock $graphics $situation $palette.BodyFont $palette.SubBrush ($x + 28) ($y + 142) 880 52 "Near"

    $mutedBrush = [System.Drawing.SolidBrush]::new($palette.SurfaceMuted)
    Fill-RoundedRect $graphics $mutedBrush ($x + 28) ($y + 210) 908 64 18
    Draw-TextBlock $graphics "다음 행동" $palette.LabelFont $palette.AccentBrush ($x + 48) ($y + 226) 120 20 "Near"
    Draw-TextBlock $graphics $lesson $palette.SmallFont $palette.TextBrush ($x + 180) ($y + 224) 730 22 "Near"

    Fill-RoundedRect $graphics $mutedBrush ($x + 28) ($y + 286) 908 38 18
    Draw-TextBlock $graphics "AI 회고" $palette.LabelFont $palette.AccentBrush ($x + 48) ($y + 295) 100 18 "Near"
    Draw-TextBlock $graphics $reflection $palette.SmallFont $palette.SubBrush ($x + 160) ($y + 294) 744 18 "Near"
    $mutedBrush.Dispose()
}

function Draw-InsightCard($graphics, $palette, [float]$x, [float]$y, [string]$title, [string]$body, $backgroundColor) {
    Draw-Card $graphics $x $y 964 188 28 $backgroundColor $palette.Border
    Draw-TextBlock $graphics $title $palette.SectionFont $palette.TextBrush ($x + 28) ($y + 26) 400 36 "Near"
    Draw-TextBlock $graphics $body $palette.BodyFont $palette.SubBrush ($x + 28) ($y + 82) 900 70 "Near"
}

function Draw-PhoneScreenshot($name, [scriptblock]$renderer) {
    $palette = New-Palette
    $canvas = New-Canvas (Join-Path $OutputDir $name)
    try {
        $canvas.Graphics.Clear($palette.Background)
        & $renderer $canvas.Graphics $palette
        Save-Canvas $canvas
    }
    finally {
        Dispose-Palette $palette
    }
}

New-Item -ItemType Directory -Path $OutputDir -Force | Out-Null

Draw-PhoneScreenshot "01-home.png" {
    param($g, $p)
    Draw-StatusBar $g $p
    Draw-AppHeader $g $p

    Draw-Card $g 34 230 1012 1430 34 $p.SurfaceCard $p.Border
    Draw-TextBlock $g "TODAY'S JOURNAL" $p.LabelFont $p.AccentBrush 72 270 300 20 "Near"
    Draw-TextBlock $g "앱을 열자마자 바로 적을 수 있게 했어요." $p.TitleFont $p.TextBrush 72 308 820 54 "Near"
    Draw-TextBlock $g "찜찜했던 순간, 말실수, 미뤄둔 행동을 바로 적어두면 저장과 동시에 AI가 기본 분석을 붙여줍니다." $p.BodyFont $p.SubBrush 72 376 880 56 "Near"

    Draw-InputField $g 72 466 936 150 "무슨 일이 있었나요?" "회의에서 급하게 말해 동료가 상처받았다. 집에 와서 생각해보니 내 감정이 먼저 나갔다." $p
    Draw-InputField $g 72 678 936 92 "어떤 감정이 남았나요?" "미안함, 조급함, 후회" $p
    Draw-InputField $g 72 832 936 116 "다음에는 어떻게 해보고 싶나요?" "바로 반응하지 말고 먼저 확인 질문 한 번 하기" $p
    Draw-Button $g 72 998 936 74 "저장하고 AI 분석 받기" $p.Accent

    Draw-TextBlock $g "RECORDS" $p.LabelFont $p.AccentBrush 72 1112 160 22 "Near"
    Draw-TextBlock $g "기록한 내용 순서대로 보기" $p.SectionFont $p.TextBrush 72 1142 460 40 "Near"
    Draw-Chip $g 72 1202 132 44 "최신순" $p.Accent $([System.Drawing.Color]::White) $true
    Draw-Chip $g 216 1202 148 44 "오래된순" $p.SurfaceCard $p.SubText $false

    Draw-EntryCard $g $p 58 1270 "관계" "3월 26일 09:41" "급하게 말해서 친구를 상처 줌" "감정이 올라왔을 때 사실 확인보다 말이 먼저 나간 순간이었다." "상대 상황부터 한 번 묻고 답하기" "상대의 의도보다 내 반응이 빨랐던 기록이에요."
    Draw-BottomNav $g $p "home"
}

Draw-PhoneScreenshot "02-records.png" {
    param($g, $p)
    Draw-StatusBar $g $p
    Draw-AppHeader $g $p

    Draw-TextBlock $g "RECORDS" $p.LabelFont $p.AccentBrush 58 224 160 22 "Near"
    Draw-TextBlock $g "기록한 내용 순서대로 보기" $p.TitleFont $p.TextBrush 58 252 500 48 "Near"
    Draw-TextBlock $g "정렬 방식을 직접 바꾸고, 저장한 후회와 AI 회고를 차례대로 다시 볼 수 있어요." $p.BodyFont $p.SubBrush 58 314 860 56 "Near"

    Draw-Chip $g 58 390 132 44 "최신순" $p.Accent $([System.Drawing.Color]::White) $true
    Draw-Chip $g 202 390 148 44 "오래된순" $p.SurfaceCard $p.SubText $false

    Draw-EntryCard $g $p 58 462 "말실수" "3월 26일 09:41" "회의에서 방어적으로 대답함" "피드백을 받자마자 변명부터 했고, 나중에 돌아보니 상대는 공격하려던 것이 아니었다." "한 문장 받아들이고 생각할 시간 벌기" "비판으로 느끼는 순간 스스로를 지키려는 반응이 빨리 나온 기록이에요."
    Draw-EntryCard $g $p 58 828 "미루기" "3월 25일 21:18" "해야 할 연락을 또 미룸" "불편한 대화를 피하고 싶어서 답장을 미뤘고 결국 더 어색해졌다." "10분 안에 짧게라도 먼저 답장 보내기" "불편함을 피한 시간이 오히려 부담을 키운 패턴이에요."

    Draw-Card $g 58 1194 964 300 30 $p.Quote $p.Border
    Draw-TextBlock $g "PREMIUM" $p.LabelFont $p.PremiumBrush 86 1226 180 22 "Near"
    Draw-TextBlock $g "심층 분석과 PDF 저장을 열어 더 깊게 돌아보세요." $p.SectionFont $p.TextBrush 86 1260 780 42 "Near"
    Draw-TextBlock $g "무료는 기본 기록과 기본 AI 요약, 프리미엄은 누적 패턴 분석과 심리 상태 리포트를 제공합니다." $p.BodyFont $p.SubBrush 86 1324 840 62 "Near"
    Draw-Button $g 86 1402 908 70 "프리미엄 구매하기" $p.Accent

    Draw-BottomNav $g $p "home"
}

Draw-PhoneScreenshot "03-insights.png" {
    param($g, $p)
    Draw-StatusBar $g $p
    Draw-AppHeader $g $p

    Draw-TextBlock $g "INSIGHT" $p.LabelFont $p.AccentBrush 58 224 160 22 "Near"
    Draw-TextBlock $g "AI 회고 화면" $p.TitleFont $p.TextBrush 58 252 400 48 "Near"
    Draw-TextBlock $g "기록된 후회를 바탕으로 반복 패턴과 다음 실천 규칙을 자동으로 정리합니다." $p.BodyFont $p.SubBrush 58 314 880 56 "Near"

    Draw-InsightCard $g $p 58 406 "반복 패턴" "최근 기록에서 가장 자주 보이는 패턴은 감정이 커질 때 말의 속도가 빨라지는 점이에요." $p.SurfaceCard
    Draw-InsightCard $g $p 58 614 "놓치기 쉬운 감정과 욕구" "안전하게 이해받고 싶다는 욕구가 커 보여요. 피드백을 공격처럼 느끼는 순간이 빨랐어요." $p.SurfaceCard
    Draw-InsightCard $g $p 58 822 "다음 행동 제안" "이번 주 규칙: 바로 답하지 말고 10초 멈추기, 확인 질문 1개 먼저 하기." $p.SurfaceCard

    Draw-Card $g 58 1030 964 248 30 $p.Quote $p.Border
    Draw-TextBlock $g "이번 주 문장" $p.LabelFont $p.PremiumBrush 86 1060 160 22 "Near"
    Draw-TextBlock $g "후회를 없애려 하지 말고, 다음 선택을 바꾸는 재료로 써보세요." $p.SectionFont $p.TextBrush 86 1100 850 88 "Near"

    Draw-Card $g 58 1310 964 188 30 $p.SurfaceCard $p.Border
    Draw-TextBlock $g "PDF 리포트 저장" $p.SectionFont $p.TextBrush 86 1344 300 36 "Near"
    Draw-TextBlock $g "프리미엄에서는 분석 리포트를 PDF로 저장해 다시 볼 수 있어요." $p.BodyFont $p.SubBrush 86 1398 720 54 "Near"
    Draw-Button $g 698 1364 296 70 "PDF 저장" $p.Accent

    Draw-BottomNav $g $p "insights"
}

Draw-PhoneScreenshot "04-archive.png" {
    param($g, $p)
    Draw-StatusBar $g $p
    Draw-AppHeader $g $p

    Draw-TextBlock $g "ARCHIVE" $p.LabelFont $p.AccentBrush 58 224 160 22 "Near"
    Draw-TextBlock $g "달력으로 다시 보는 기록" $p.TitleFont $p.TextBrush 58 252 520 48 "Near"
    Draw-TextBlock $g "날짜를 누르면 그날의 기록 목록과 상세 내용을 바로 확인할 수 있어요." $p.BodyFont $p.SubBrush 58 314 880 56 "Near"

    Draw-Card $g 58 404 964 450 30 $p.SurfaceCard $p.Border
    Draw-TextBlock $g "2026년 3월" $p.SectionFont $p.TextBrush 86 438 240 36 "Near"
    $weekdays = @("일", "월", "화", "수", "목", "금", "토")
    for ($i = 0; $i -lt $weekdays.Count; $i++) {
        Draw-TextBlock $g $weekdays[$i] $p.SmallFont $p.SubBrush (96 + ($i * 132)) 500 60 22 "Center"
    }
    $dates = 1..31
    $startCol = 0
    $row = 0
    for ($index = 0; $index -lt $dates.Count; $index++) {
        $col = ($startCol + $index) % 7
        $row = [math]::Floor(($startCol + $index) / 7)
        $x = 100 + ($col * 132)
        $y = 548 + ($row * 58)
        $isSelected = $dates[$index] -eq 26
        $hasEntry = $dates[$index] -in @(21, 24, 25, 26)
        if ($isSelected) {
            $accentBrush = [System.Drawing.SolidBrush]::new($p.Accent)
            Fill-RoundedRect $g $accentBrush ($x - 18) ($y - 8) 72 46 16
            $accentBrush.Dispose()
        } elseif ($hasEntry) {
            $softBrush = [System.Drawing.SolidBrush]::new($p.AccentSoft)
            Fill-RoundedRect $g $softBrush ($x - 18) ($y - 8) 72 46 16
            $softBrush.Dispose()
        }
        $numberBrush = if ($isSelected) { $p.WhiteBrush } else { $p.TextBrush }
        Draw-TextBlock $g ([string]$dates[$index]) $p.SmallFont $numberBrush ($x - 10) $y 42 22 "Center"
        if ($hasEntry) {
            $dotBrush = if ($isSelected) { $p.WhiteBrush } else { $p.AccentBrush }
            $g.FillEllipse($dotBrush, $x + 8, $y + 24, 8, 8)
        }
    }

    Draw-Card $g 58 884 964 264 30 $p.SurfaceCard $p.Border
    Draw-TextBlock $g "2026년 3월 26일" $p.SectionFont $p.TextBrush 86 918 260 36 "Near"
    Draw-Card $g 86 982 908 64 18 $p.AccentSoft $p.Border
    Draw-TextBlock $g "급하게 말해서 친구를 상처 줌" $p.BodyFont $p.TextBrush 110 1004 520 24 "Near"
    Draw-TextBlock $g "3월 26일 09:41 · 관계" $p.SmallFont $p.SubBrush 110 1030 300 20 "Near"
    Draw-Card $g 86 1064 908 64 18 $p.SurfaceMuted $p.Border
    Draw-TextBlock $g "회의에서 방어적으로 대답함" $p.BodyFont $p.TextBrush 110 1086 520 24 "Near"
    Draw-TextBlock $g "3월 26일 08:10 · 말실수" $p.SmallFont $p.SubBrush 110 1112 320 20 "Near"

    Draw-Card $g 58 1180 964 486 30 $p.SurfaceCard $p.Border
    Draw-TextBlock $g "급하게 말해서 친구를 상처 줌" $p.SectionFont $p.TextBrush 86 1216 600 36 "Near"
    Draw-TextBlock $g "상황" $p.LabelFont $p.AccentBrush 86 1272 120 22 "Near"
    Draw-TextBlock $g "친구가 늦었다고 짜증 섞인 말을 했는데, 돌아보니 그날 친구는 힘든 일이 있었다." $p.BodyFont $p.SubBrush 86 1304 820 44 "Near"
    Draw-TextBlock $g "감정" $p.LabelFont $p.AccentBrush 86 1368 120 22 "Near"
    Draw-TextBlock $g "후회, 미안함, 조급함" $p.BodyFont $p.TextBrush 86 1400 420 22 "Near"
    Draw-TextBlock $g "AI 분석" $p.LabelFont $p.AccentBrush 86 1456 120 22 "Near"
    Draw-TextBlock $g "상대의 상황을 확인하기 전에 감정이 먼저 나간 패턴이에요." $p.BodyFont $p.SubBrush 86 1488 840 44 "Near"

    Draw-BottomNav $g $p "archive"
}

Draw-PhoneScreenshot "05-setup.png" {
    param($g, $p)
    Draw-StatusBar $g $p

    Draw-TextBlock $g "마음정리 일기" $p.TitleFont $p.TextBrush 58 110 520 44 "Near"
    Draw-TextBlock $g "처음 사용할 때 한 번만 간단히 설명드릴게요." $p.BodyFont $p.SubBrush 58 162 720 30 "Near"

    Draw-Card $g 58 242 964 172 30 $p.SurfaceCard $p.Border
    Draw-TextBlock $g "언어 선택" $p.LabelFont $p.AccentBrush 86 274 180 22 "Near"
    Draw-TextBlock $g "앱 시작 언어를 정하고 나중에 설정에서 다시 바꿀 수 있어요." $p.BodyFont $p.SubBrush 86 308 760 28 "Near"
    Draw-Chip $g 86 352 144 44 "한국어" $p.Accent $([System.Drawing.Color]::White) $true
    Draw-Chip $g 244 352 144 44 "English" $p.SurfaceCard $p.SubText $false

    Draw-Card $g 58 442 964 190 30 $p.SurfaceCard $p.Border
    Draw-TextBlock $g "처음부터 앱 잠금 사용할까요?" $p.LabelFont $p.AccentBrush 86 474 300 22 "Near"
    Draw-TextBlock $g "지문이나 얼굴 인식으로 잠금을 사용할 수 있고, 나중에 설정에서 해제하거나 다시 켤 수 있어요." $p.BodyFont $p.SubBrush 86 508 820 56 "Near"
    Draw-Chip $g 86 576 140 44 "사용 안 함" $p.SurfaceCard $p.SubText $false
    Draw-Chip $g 242 576 128 44 "사용함" $p.Accent $([System.Drawing.Color]::White) $true

    Draw-Card $g 58 660 964 162 30 $p.SurfaceCard $p.Border
    Draw-TextBlock $g "1. 홈에서 바로 기록" $p.LabelFont $p.AccentBrush 86 694 220 22 "Near"
    Draw-TextBlock $g "앱을 열면 바로 일기를 쓸 수 있고, 저장하면 AI가 기본 회고를 자동으로 붙여줍니다." $p.BodyFont $p.SubBrush 86 726 820 52 "Near"

    Draw-Card $g 58 844 964 162 30 $p.SurfaceCard $p.Border
    Draw-TextBlock $g "2. 인사이트와 보관함" $p.LabelFont $p.AccentBrush 86 878 220 22 "Near"
    Draw-TextBlock $g "인사이트에서는 반복 패턴을 보고, 보관함에서는 날짜별로 기록을 다시 확인할 수 있어요." $p.BodyFont $p.SubBrush 86 910 820 52 "Near"

    Draw-Card $g 58 1028 964 162 30 $p.SurfaceCard $p.Border
    Draw-TextBlock $g "3. 설정에서 잠금 관리" $p.LabelFont $p.AccentBrush 86 1062 260 22 "Near"
    Draw-TextBlock $g "설정 탭에서 앱 잠금을 켜거나 끌 수 있고, 튜토리얼도 다시 볼 수 있어요." $p.BodyFont $p.SubBrush 86 1094 820 52 "Near"

    Draw-Button $g 58 1230 964 78 "바로 시작하기" $p.Accent
}

Draw-PhoneScreenshot "06-settings.png" {
    param($g, $p)
    Draw-StatusBar $g $p
    Draw-AppHeader $g $p

    Draw-TextBlock $g "SETTINGS" $p.LabelFont $p.AccentBrush 58 224 160 22 "Near"
    Draw-TextBlock $g "사용 방식과 잠금 설정" $p.TitleFont $p.TextBrush 58 252 520 48 "Near"
    Draw-TextBlock $g "언어, 잠금, 튜토리얼, 프리미엄 미리보기를 한 곳에서 관리할 수 있어요." $p.BodyFont $p.SubBrush 58 314 900 56 "Near"

    Draw-Card $g 58 404 964 222 30 $p.SurfaceCard $p.Border
    Draw-TextBlock $g "언어" $p.SectionFont $p.TextBrush 86 438 160 36 "Near"
    Draw-TextBlock $g "한국어와 영어를 바로 전환할 수 있어요." $p.BodyFont $p.SubBrush 86 492 540 28 "Near"
    Draw-Chip $g 86 548 144 44 "한국어" $p.Accent $([System.Drawing.Color]::White) $true
    Draw-Chip $g 244 548 144 44 "English" $p.SurfaceCard $p.SubText $false

    Draw-Card $g 58 648 964 226 30 $p.SurfaceCard $p.Border
    Draw-TextBlock $g "앱 잠금" $p.SectionFont $p.TextBrush 86 682 160 36 "Near"
    Draw-TextBlock $g "지문 또는 얼굴 인식으로 앱을 잠글 수 있어요." $p.BodyFont $p.SubBrush 86 736 560 28 "Near"
    Draw-Button $g 698 720 296 70 "앱 잠금 끄기" $p.Accent

    Draw-Card $g 58 896 964 214 30 $p.SurfaceCard $p.Border
    Draw-TextBlock $g "사용 안내" $p.SectionFont $p.TextBrush 86 930 160 36 "Near"
    Draw-TextBlock $g "튜토리얼을 다시 열어서 기록 방식과 보관함 사용법을 확인할 수 있어요." $p.BodyFont $p.SubBrush 86 984 760 52 "Near"
    Draw-Button $g 698 972 296 70 "튜토리얼 다시 보기" $p.Accent

    Draw-Card $g 58 1134 964 392 30 $p.Quote $p.Border
    Draw-TextBlock $g "PREMIUM ACTIVE" $p.LabelFont $p.PremiumBrush 86 1166 220 22 "Near"
    Draw-TextBlock $g "구매가 확인되어 심층 분석과 PDF 저장이 활성화되어 있어요." $p.SectionFont $p.TextBrush 86 1204 820 42 "Near"
    Draw-TextBlock $g "무료: 기록 저장, 최근 기록, 기본 AI 요약" $p.BodyFont $p.SubBrush 86 1270 720 28 "Near"
    Draw-TextBlock $g "유료: 누적 패턴 분석, 심리 상태 리포트, 심층 AI 회고, PDF 저장" $p.BodyFont $p.TextBrush 86 1310 820 56 "Near"
    Draw-Button $g 86 1416 908 70 "구매 확인됨" $p.Accent

    Draw-BottomNav $g $p "settings"
}
