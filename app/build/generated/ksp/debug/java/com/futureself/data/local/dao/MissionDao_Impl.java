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
import com.futureself.data.local.entity.DailyMission;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class MissionDao_Impl implements MissionDao {
  private final RoomDatabase __db;

  private final EntityUpsertionAdapter<DailyMission> __upsertionAdapterOfDailyMission;

  public MissionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__upsertionAdapterOfDailyMission = new EntityUpsertionAdapter<DailyMission>(new EntityInsertionAdapter<DailyMission>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT INTO `daily_missions` (`id`,`journalId`,`mission`,`why`,`duration`,`domain`,`rewardSource`,`isCompleted`,`createdAt`,`completedAt`) VALUES (?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DailyMission entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getJournalId());
        statement.bindString(3, entity.getMission());
        statement.bindString(4, entity.getWhy());
        statement.bindString(5, entity.getDuration());
        statement.bindString(6, entity.getDomain());
        if (entity.getRewardSource() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getRewardSource());
        }
        final int _tmp = entity.isCompleted() ? 1 : 0;
        statement.bindLong(8, _tmp);
        statement.bindLong(9, entity.getCreatedAt());
        if (entity.getCompletedAt() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getCompletedAt());
        }
      }
    }, new EntityDeletionOrUpdateAdapter<DailyMission>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE `daily_missions` SET `id` = ?,`journalId` = ?,`mission` = ?,`why` = ?,`duration` = ?,`domain` = ?,`rewardSource` = ?,`isCompleted` = ?,`createdAt` = ?,`completedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DailyMission entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getJournalId());
        statement.bindString(3, entity.getMission());
        statement.bindString(4, entity.getWhy());
        statement.bindString(5, entity.getDuration());
        statement.bindString(6, entity.getDomain());
        if (entity.getRewardSource() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getRewardSource());
        }
        final int _tmp = entity.isCompleted() ? 1 : 0;
        statement.bindLong(8, _tmp);
        statement.bindLong(9, entity.getCreatedAt());
        if (entity.getCompletedAt() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getCompletedAt());
        }
        statement.bindString(11, entity.getId());
      }
    });
  }

  @Override
  public Object upsert(final DailyMission mission, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfDailyMission.upsert(mission);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object findByJournalId(final String journalId,
      final Continuation<? super DailyMission> $completion) {
    final String _sql = "SELECT * FROM daily_missions WHERE journalId = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, journalId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DailyMission>() {
      @Override
      @Nullable
      public DailyMission call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfJournalId = CursorUtil.getColumnIndexOrThrow(_cursor, "journalId");
          final int _cursorIndexOfMission = CursorUtil.getColumnIndexOrThrow(_cursor, "mission");
          final int _cursorIndexOfWhy = CursorUtil.getColumnIndexOrThrow(_cursor, "why");
          final int _cursorIndexOfDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "duration");
          final int _cursorIndexOfDomain = CursorUtil.getColumnIndexOrThrow(_cursor, "domain");
          final int _cursorIndexOfRewardSource = CursorUtil.getColumnIndexOrThrow(_cursor, "rewardSource");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final DailyMission _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpJournalId;
            _tmpJournalId = _cursor.getString(_cursorIndexOfJournalId);
            final String _tmpMission;
            _tmpMission = _cursor.getString(_cursorIndexOfMission);
            final String _tmpWhy;
            _tmpWhy = _cursor.getString(_cursorIndexOfWhy);
            final String _tmpDuration;
            _tmpDuration = _cursor.getString(_cursorIndexOfDuration);
            final String _tmpDomain;
            _tmpDomain = _cursor.getString(_cursorIndexOfDomain);
            final String _tmpRewardSource;
            if (_cursor.isNull(_cursorIndexOfRewardSource)) {
              _tmpRewardSource = null;
            } else {
              _tmpRewardSource = _cursor.getString(_cursorIndexOfRewardSource);
            }
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            _result = new DailyMission(_tmpId,_tmpJournalId,_tmpMission,_tmpWhy,_tmpDuration,_tmpDomain,_tmpRewardSource,_tmpIsCompleted,_tmpCreatedAt,_tmpCompletedAt);
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

  @Override
  public Flow<List<DailyMission>> observeOpenMissions() {
    final String _sql = "SELECT * FROM daily_missions WHERE isCompleted = 0 ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"daily_missions"}, new Callable<List<DailyMission>>() {
      @Override
      @NonNull
      public List<DailyMission> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfJournalId = CursorUtil.getColumnIndexOrThrow(_cursor, "journalId");
          final int _cursorIndexOfMission = CursorUtil.getColumnIndexOrThrow(_cursor, "mission");
          final int _cursorIndexOfWhy = CursorUtil.getColumnIndexOrThrow(_cursor, "why");
          final int _cursorIndexOfDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "duration");
          final int _cursorIndexOfDomain = CursorUtil.getColumnIndexOrThrow(_cursor, "domain");
          final int _cursorIndexOfRewardSource = CursorUtil.getColumnIndexOrThrow(_cursor, "rewardSource");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final List<DailyMission> _result = new ArrayList<DailyMission>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailyMission _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpJournalId;
            _tmpJournalId = _cursor.getString(_cursorIndexOfJournalId);
            final String _tmpMission;
            _tmpMission = _cursor.getString(_cursorIndexOfMission);
            final String _tmpWhy;
            _tmpWhy = _cursor.getString(_cursorIndexOfWhy);
            final String _tmpDuration;
            _tmpDuration = _cursor.getString(_cursorIndexOfDuration);
            final String _tmpDomain;
            _tmpDomain = _cursor.getString(_cursorIndexOfDomain);
            final String _tmpRewardSource;
            if (_cursor.isNull(_cursorIndexOfRewardSource)) {
              _tmpRewardSource = null;
            } else {
              _tmpRewardSource = _cursor.getString(_cursorIndexOfRewardSource);
            }
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            _item = new DailyMission(_tmpId,_tmpJournalId,_tmpMission,_tmpWhy,_tmpDuration,_tmpDomain,_tmpRewardSource,_tmpIsCompleted,_tmpCreatedAt,_tmpCompletedAt);
            _result.add(_item);
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
  public Flow<List<DailyMission>> observeAll() {
    final String _sql = "SELECT * FROM daily_missions ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"daily_missions"}, new Callable<List<DailyMission>>() {
      @Override
      @NonNull
      public List<DailyMission> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfJournalId = CursorUtil.getColumnIndexOrThrow(_cursor, "journalId");
          final int _cursorIndexOfMission = CursorUtil.getColumnIndexOrThrow(_cursor, "mission");
          final int _cursorIndexOfWhy = CursorUtil.getColumnIndexOrThrow(_cursor, "why");
          final int _cursorIndexOfDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "duration");
          final int _cursorIndexOfDomain = CursorUtil.getColumnIndexOrThrow(_cursor, "domain");
          final int _cursorIndexOfRewardSource = CursorUtil.getColumnIndexOrThrow(_cursor, "rewardSource");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final List<DailyMission> _result = new ArrayList<DailyMission>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailyMission _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpJournalId;
            _tmpJournalId = _cursor.getString(_cursorIndexOfJournalId);
            final String _tmpMission;
            _tmpMission = _cursor.getString(_cursorIndexOfMission);
            final String _tmpWhy;
            _tmpWhy = _cursor.getString(_cursorIndexOfWhy);
            final String _tmpDuration;
            _tmpDuration = _cursor.getString(_cursorIndexOfDuration);
            final String _tmpDomain;
            _tmpDomain = _cursor.getString(_cursorIndexOfDomain);
            final String _tmpRewardSource;
            if (_cursor.isNull(_cursorIndexOfRewardSource)) {
              _tmpRewardSource = null;
            } else {
              _tmpRewardSource = _cursor.getString(_cursorIndexOfRewardSource);
            }
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            _item = new DailyMission(_tmpId,_tmpJournalId,_tmpMission,_tmpWhy,_tmpDuration,_tmpDomain,_tmpRewardSource,_tmpIsCompleted,_tmpCreatedAt,_tmpCompletedAt);
            _result.add(_item);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
