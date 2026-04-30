package com.futureself.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.EntityUpsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.futureself.data.local.entity.UserProfile;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class UserProfileDao_Impl implements UserProfileDao {
  private final RoomDatabase __db;

  private final EntityUpsertionAdapter<UserProfile> __upsertionAdapterOfUserProfile;

  public UserProfileDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__upsertionAdapterOfUserProfile = new EntityUpsertionAdapter<UserProfile>(new EntityInsertionAdapter<UserProfile>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT INTO `user_profile` (`id`,`focusArea`,`changeWish`,`futureImage5y`,`mainObstacle`,`strength`,`priorityDomains`,`preferredLanguageTag`,`countryCode`,`timeZoneId`,`onboardingCompleted`,`totalJournalCount`,`currentStreak`,`lastActiveDate`,`cloudSyncEnabled`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserProfile entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getFocusArea());
        statement.bindString(3, entity.getChangeWish());
        statement.bindString(4, entity.getFutureImage5y());
        statement.bindString(5, entity.getMainObstacle());
        statement.bindString(6, entity.getStrength());
        statement.bindString(7, entity.getPriorityDomains());
        statement.bindString(8, entity.getPreferredLanguageTag());
        statement.bindString(9, entity.getCountryCode());
        statement.bindString(10, entity.getTimeZoneId());
        final int _tmp = entity.getOnboardingCompleted() ? 1 : 0;
        statement.bindLong(11, _tmp);
        statement.bindLong(12, entity.getTotalJournalCount());
        statement.bindLong(13, entity.getCurrentStreak());
        statement.bindLong(14, entity.getLastActiveDate());
        final int _tmp_1 = entity.getCloudSyncEnabled() ? 1 : 0;
        statement.bindLong(15, _tmp_1);
      }
    }, new EntityDeletionOrUpdateAdapter<UserProfile>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE `user_profile` SET `id` = ?,`focusArea` = ?,`changeWish` = ?,`futureImage5y` = ?,`mainObstacle` = ?,`strength` = ?,`priorityDomains` = ?,`preferredLanguageTag` = ?,`countryCode` = ?,`timeZoneId` = ?,`onboardingCompleted` = ?,`totalJournalCount` = ?,`currentStreak` = ?,`lastActiveDate` = ?,`cloudSyncEnabled` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserProfile entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getFocusArea());
        statement.bindString(3, entity.getChangeWish());
        statement.bindString(4, entity.getFutureImage5y());
        statement.bindString(5, entity.getMainObstacle());
        statement.bindString(6, entity.getStrength());
        statement.bindString(7, entity.getPriorityDomains());
        statement.bindString(8, entity.getPreferredLanguageTag());
        statement.bindString(9, entity.getCountryCode());
        statement.bindString(10, entity.getTimeZoneId());
        final int _tmp = entity.getOnboardingCompleted() ? 1 : 0;
        statement.bindLong(11, _tmp);
        statement.bindLong(12, entity.getTotalJournalCount());
        statement.bindLong(13, entity.getCurrentStreak());
        statement.bindLong(14, entity.getLastActiveDate());
        final int _tmp_1 = entity.getCloudSyncEnabled() ? 1 : 0;
        statement.bindLong(15, _tmp_1);
        statement.bindString(16, entity.getId());
      }
    });
  }

  @Override
  public Object upsert(final UserProfile profile, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfUserProfile.upsert(profile);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<UserProfile> observe(final String id) {
    final String _sql = "SELECT * FROM user_profile WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"user_profile"}, new Callable<UserProfile>() {
      @Override
      @Nullable
      public UserProfile call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFocusArea = CursorUtil.getColumnIndexOrThrow(_cursor, "focusArea");
          final int _cursorIndexOfChangeWish = CursorUtil.getColumnIndexOrThrow(_cursor, "changeWish");
          final int _cursorIndexOfFutureImage5y = CursorUtil.getColumnIndexOrThrow(_cursor, "futureImage5y");
          final int _cursorIndexOfMainObstacle = CursorUtil.getColumnIndexOrThrow(_cursor, "mainObstacle");
          final int _cursorIndexOfStrength = CursorUtil.getColumnIndexOrThrow(_cursor, "strength");
          final int _cursorIndexOfPriorityDomains = CursorUtil.getColumnIndexOrThrow(_cursor, "priorityDomains");
          final int _cursorIndexOfPreferredLanguageTag = CursorUtil.getColumnIndexOrThrow(_cursor, "preferredLanguageTag");
          final int _cursorIndexOfCountryCode = CursorUtil.getColumnIndexOrThrow(_cursor, "countryCode");
          final int _cursorIndexOfTimeZoneId = CursorUtil.getColumnIndexOrThrow(_cursor, "timeZoneId");
          final int _cursorIndexOfOnboardingCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "onboardingCompleted");
          final int _cursorIndexOfTotalJournalCount = CursorUtil.getColumnIndexOrThrow(_cursor, "totalJournalCount");
          final int _cursorIndexOfCurrentStreak = CursorUtil.getColumnIndexOrThrow(_cursor, "currentStreak");
          final int _cursorIndexOfLastActiveDate = CursorUtil.getColumnIndexOrThrow(_cursor, "lastActiveDate");
          final int _cursorIndexOfCloudSyncEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "cloudSyncEnabled");
          final UserProfile _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpFocusArea;
            _tmpFocusArea = _cursor.getString(_cursorIndexOfFocusArea);
            final String _tmpChangeWish;
            _tmpChangeWish = _cursor.getString(_cursorIndexOfChangeWish);
            final String _tmpFutureImage5y;
            _tmpFutureImage5y = _cursor.getString(_cursorIndexOfFutureImage5y);
            final String _tmpMainObstacle;
            _tmpMainObstacle = _cursor.getString(_cursorIndexOfMainObstacle);
            final String _tmpStrength;
            _tmpStrength = _cursor.getString(_cursorIndexOfStrength);
            final String _tmpPriorityDomains;
            _tmpPriorityDomains = _cursor.getString(_cursorIndexOfPriorityDomains);
            final String _tmpPreferredLanguageTag;
            _tmpPreferredLanguageTag = _cursor.getString(_cursorIndexOfPreferredLanguageTag);
            final String _tmpCountryCode;
            _tmpCountryCode = _cursor.getString(_cursorIndexOfCountryCode);
            final String _tmpTimeZoneId;
            _tmpTimeZoneId = _cursor.getString(_cursorIndexOfTimeZoneId);
            final boolean _tmpOnboardingCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfOnboardingCompleted);
            _tmpOnboardingCompleted = _tmp != 0;
            final int _tmpTotalJournalCount;
            _tmpTotalJournalCount = _cursor.getInt(_cursorIndexOfTotalJournalCount);
            final int _tmpCurrentStreak;
            _tmpCurrentStreak = _cursor.getInt(_cursorIndexOfCurrentStreak);
            final long _tmpLastActiveDate;
            _tmpLastActiveDate = _cursor.getLong(_cursorIndexOfLastActiveDate);
            final boolean _tmpCloudSyncEnabled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfCloudSyncEnabled);
            _tmpCloudSyncEnabled = _tmp_1 != 0;
            _result = new UserProfile(_tmpId,_tmpFocusArea,_tmpChangeWish,_tmpFutureImage5y,_tmpMainObstacle,_tmpStrength,_tmpPriorityDomains,_tmpPreferredLanguageTag,_tmpCountryCode,_tmpTimeZoneId,_tmpOnboardingCompleted,_tmpTotalJournalCount,_tmpCurrentStreak,_tmpLastActiveDate,_tmpCloudSyncEnabled);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object findById(final String id, final Continuation<? super UserProfile> $completion) {
    final String _sql = "SELECT * FROM user_profile WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<UserProfile>() {
      @Override
      @Nullable
      public UserProfile call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFocusArea = CursorUtil.getColumnIndexOrThrow(_cursor, "focusArea");
          final int _cursorIndexOfChangeWish = CursorUtil.getColumnIndexOrThrow(_cursor, "changeWish");
          final int _cursorIndexOfFutureImage5y = CursorUtil.getColumnIndexOrThrow(_cursor, "futureImage5y");
          final int _cursorIndexOfMainObstacle = CursorUtil.getColumnIndexOrThrow(_cursor, "mainObstacle");
          final int _cursorIndexOfStrength = CursorUtil.getColumnIndexOrThrow(_cursor, "strength");
          final int _cursorIndexOfPriorityDomains = CursorUtil.getColumnIndexOrThrow(_cursor, "priorityDomains");
          final int _cursorIndexOfPreferredLanguageTag = CursorUtil.getColumnIndexOrThrow(_cursor, "preferredLanguageTag");
          final int _cursorIndexOfCountryCode = CursorUtil.getColumnIndexOrThrow(_cursor, "countryCode");
          final int _cursorIndexOfTimeZoneId = CursorUtil.getColumnIndexOrThrow(_cursor, "timeZoneId");
          final int _cursorIndexOfOnboardingCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "onboardingCompleted");
          final int _cursorIndexOfTotalJournalCount = CursorUtil.getColumnIndexOrThrow(_cursor, "totalJournalCount");
          final int _cursorIndexOfCurrentStreak = CursorUtil.getColumnIndexOrThrow(_cursor, "currentStreak");
          final int _cursorIndexOfLastActiveDate = CursorUtil.getColumnIndexOrThrow(_cursor, "lastActiveDate");
          final int _cursorIndexOfCloudSyncEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "cloudSyncEnabled");
          final UserProfile _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpFocusArea;
            _tmpFocusArea = _cursor.getString(_cursorIndexOfFocusArea);
            final String _tmpChangeWish;
            _tmpChangeWish = _cursor.getString(_cursorIndexOfChangeWish);
            final String _tmpFutureImage5y;
            _tmpFutureImage5y = _cursor.getString(_cursorIndexOfFutureImage5y);
            final String _tmpMainObstacle;
            _tmpMainObstacle = _cursor.getString(_cursorIndexOfMainObstacle);
            final String _tmpStrength;
            _tmpStrength = _cursor.getString(_cursorIndexOfStrength);
            final String _tmpPriorityDomains;
            _tmpPriorityDomains = _cursor.getString(_cursorIndexOfPriorityDomains);
            final String _tmpPreferredLanguageTag;
            _tmpPreferredLanguageTag = _cursor.getString(_cursorIndexOfPreferredLanguageTag);
            final String _tmpCountryCode;
            _tmpCountryCode = _cursor.getString(_cursorIndexOfCountryCode);
            final String _tmpTimeZoneId;
            _tmpTimeZoneId = _cursor.getString(_cursorIndexOfTimeZoneId);
            final boolean _tmpOnboardingCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfOnboardingCompleted);
            _tmpOnboardingCompleted = _tmp != 0;
            final int _tmpTotalJournalCount;
            _tmpTotalJournalCount = _cursor.getInt(_cursorIndexOfTotalJournalCount);
            final int _tmpCurrentStreak;
            _tmpCurrentStreak = _cursor.getInt(_cursorIndexOfCurrentStreak);
            final long _tmpLastActiveDate;
            _tmpLastActiveDate = _cursor.getLong(_cursorIndexOfLastActiveDate);
            final boolean _tmpCloudSyncEnabled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfCloudSyncEnabled);
            _tmpCloudSyncEnabled = _tmp_1 != 0;
            _result = new UserProfile(_tmpId,_tmpFocusArea,_tmpChangeWish,_tmpFutureImage5y,_tmpMainObstacle,_tmpStrength,_tmpPriorityDomains,_tmpPreferredLanguageTag,_tmpCountryCode,_tmpTimeZoneId,_tmpOnboardingCompleted,_tmpTotalJournalCount,_tmpCurrentStreak,_tmpLastActiveDate,_tmpCloudSyncEnabled);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
