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
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.futureself.data.local.entity.JournalEntry;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
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
public final class JournalDao_Impl implements JournalDao {
  private final RoomDatabase __db;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  private final EntityUpsertionAdapter<JournalEntry> __upsertionAdapterOfJournalEntry;

  public JournalDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM journal_entries WHERE id = ?";
        return _query;
      }
    };
    this.__upsertionAdapterOfJournalEntry = new EntityUpsertionAdapter<JournalEntry>(new EntityInsertionAdapter<JournalEntry>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT INTO `journal_entries` (`id`,`userId`,`content`,`domain`,`targetYear`,`targetYearLabel`,`createdAt`,`languageTag`,`obstacle`,`emotion`,`ifThenPlan`,`missionId`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final JournalEntry entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getUserId());
        statement.bindString(3, entity.getContent());
        statement.bindString(4, entity.getDomain());
        statement.bindLong(5, entity.getTargetYear());
        statement.bindString(6, entity.getTargetYearLabel());
        statement.bindLong(7, entity.getCreatedAt());
        statement.bindString(8, entity.getLanguageTag());
        if (entity.getObstacle() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getObstacle());
        }
        if (entity.getEmotion() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getEmotion());
        }
        if (entity.getIfThenPlan() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getIfThenPlan());
        }
        if (entity.getMissionId() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getMissionId());
        }
      }
    }, new EntityDeletionOrUpdateAdapter<JournalEntry>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE `journal_entries` SET `id` = ?,`userId` = ?,`content` = ?,`domain` = ?,`targetYear` = ?,`targetYearLabel` = ?,`createdAt` = ?,`languageTag` = ?,`obstacle` = ?,`emotion` = ?,`ifThenPlan` = ?,`missionId` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final JournalEntry entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getUserId());
        statement.bindString(3, entity.getContent());
        statement.bindString(4, entity.getDomain());
        statement.bindLong(5, entity.getTargetYear());
        statement.bindString(6, entity.getTargetYearLabel());
        statement.bindLong(7, entity.getCreatedAt());
        statement.bindString(8, entity.getLanguageTag());
        if (entity.getObstacle() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getObstacle());
        }
        if (entity.getEmotion() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getEmotion());
        }
        if (entity.getIfThenPlan() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getIfThenPlan());
        }
        if (entity.getMissionId() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getMissionId());
        }
        statement.bindString(13, entity.getId());
      }
    });
  }

  @Override
  public Object deleteById(final String id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object upsert(final JournalEntry entry, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfJournalEntry.upsert(entry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<JournalEntry>> observeAll(final String userId) {
    final String _sql = "SELECT * FROM journal_entries WHERE userId = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"journal_entries"}, new Callable<List<JournalEntry>>() {
      @Override
      @NonNull
      public List<JournalEntry> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfDomain = CursorUtil.getColumnIndexOrThrow(_cursor, "domain");
          final int _cursorIndexOfTargetYear = CursorUtil.getColumnIndexOrThrow(_cursor, "targetYear");
          final int _cursorIndexOfTargetYearLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "targetYearLabel");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfLanguageTag = CursorUtil.getColumnIndexOrThrow(_cursor, "languageTag");
          final int _cursorIndexOfObstacle = CursorUtil.getColumnIndexOrThrow(_cursor, "obstacle");
          final int _cursorIndexOfEmotion = CursorUtil.getColumnIndexOrThrow(_cursor, "emotion");
          final int _cursorIndexOfIfThenPlan = CursorUtil.getColumnIndexOrThrow(_cursor, "ifThenPlan");
          final int _cursorIndexOfMissionId = CursorUtil.getColumnIndexOrThrow(_cursor, "missionId");
          final List<JournalEntry> _result = new ArrayList<JournalEntry>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final JournalEntry _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpDomain;
            _tmpDomain = _cursor.getString(_cursorIndexOfDomain);
            final int _tmpTargetYear;
            _tmpTargetYear = _cursor.getInt(_cursorIndexOfTargetYear);
            final String _tmpTargetYearLabel;
            _tmpTargetYearLabel = _cursor.getString(_cursorIndexOfTargetYearLabel);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpLanguageTag;
            _tmpLanguageTag = _cursor.getString(_cursorIndexOfLanguageTag);
            final String _tmpObstacle;
            if (_cursor.isNull(_cursorIndexOfObstacle)) {
              _tmpObstacle = null;
            } else {
              _tmpObstacle = _cursor.getString(_cursorIndexOfObstacle);
            }
            final String _tmpEmotion;
            if (_cursor.isNull(_cursorIndexOfEmotion)) {
              _tmpEmotion = null;
            } else {
              _tmpEmotion = _cursor.getString(_cursorIndexOfEmotion);
            }
            final String _tmpIfThenPlan;
            if (_cursor.isNull(_cursorIndexOfIfThenPlan)) {
              _tmpIfThenPlan = null;
            } else {
              _tmpIfThenPlan = _cursor.getString(_cursorIndexOfIfThenPlan);
            }
            final String _tmpMissionId;
            if (_cursor.isNull(_cursorIndexOfMissionId)) {
              _tmpMissionId = null;
            } else {
              _tmpMissionId = _cursor.getString(_cursorIndexOfMissionId);
            }
            _item = new JournalEntry(_tmpId,_tmpUserId,_tmpContent,_tmpDomain,_tmpTargetYear,_tmpTargetYearLabel,_tmpCreatedAt,_tmpLanguageTag,_tmpObstacle,_tmpEmotion,_tmpIfThenPlan,_tmpMissionId);
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
  public Flow<List<JournalEntry>> observeTimeline(final String userId, final List<Integer> years) {
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT * FROM journal_entries WHERE userId = ");
    _stringBuilder.append("?");
    _stringBuilder.append(" AND targetYear IN (");
    final int _inputSize = years.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(") ORDER BY targetYear ASC, createdAt DESC");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 1 + _inputSize;
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    _argIndex = 2;
    for (int _item : years) {
      _statement.bindLong(_argIndex, _item);
      _argIndex++;
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"journal_entries"}, new Callable<List<JournalEntry>>() {
      @Override
      @NonNull
      public List<JournalEntry> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfDomain = CursorUtil.getColumnIndexOrThrow(_cursor, "domain");
          final int _cursorIndexOfTargetYear = CursorUtil.getColumnIndexOrThrow(_cursor, "targetYear");
          final int _cursorIndexOfTargetYearLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "targetYearLabel");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfLanguageTag = CursorUtil.getColumnIndexOrThrow(_cursor, "languageTag");
          final int _cursorIndexOfObstacle = CursorUtil.getColumnIndexOrThrow(_cursor, "obstacle");
          final int _cursorIndexOfEmotion = CursorUtil.getColumnIndexOrThrow(_cursor, "emotion");
          final int _cursorIndexOfIfThenPlan = CursorUtil.getColumnIndexOrThrow(_cursor, "ifThenPlan");
          final int _cursorIndexOfMissionId = CursorUtil.getColumnIndexOrThrow(_cursor, "missionId");
          final List<JournalEntry> _result = new ArrayList<JournalEntry>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final JournalEntry _item_1;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpDomain;
            _tmpDomain = _cursor.getString(_cursorIndexOfDomain);
            final int _tmpTargetYear;
            _tmpTargetYear = _cursor.getInt(_cursorIndexOfTargetYear);
            final String _tmpTargetYearLabel;
            _tmpTargetYearLabel = _cursor.getString(_cursorIndexOfTargetYearLabel);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpLanguageTag;
            _tmpLanguageTag = _cursor.getString(_cursorIndexOfLanguageTag);
            final String _tmpObstacle;
            if (_cursor.isNull(_cursorIndexOfObstacle)) {
              _tmpObstacle = null;
            } else {
              _tmpObstacle = _cursor.getString(_cursorIndexOfObstacle);
            }
            final String _tmpEmotion;
            if (_cursor.isNull(_cursorIndexOfEmotion)) {
              _tmpEmotion = null;
            } else {
              _tmpEmotion = _cursor.getString(_cursorIndexOfEmotion);
            }
            final String _tmpIfThenPlan;
            if (_cursor.isNull(_cursorIndexOfIfThenPlan)) {
              _tmpIfThenPlan = null;
            } else {
              _tmpIfThenPlan = _cursor.getString(_cursorIndexOfIfThenPlan);
            }
            final String _tmpMissionId;
            if (_cursor.isNull(_cursorIndexOfMissionId)) {
              _tmpMissionId = null;
            } else {
              _tmpMissionId = _cursor.getString(_cursorIndexOfMissionId);
            }
            _item_1 = new JournalEntry(_tmpId,_tmpUserId,_tmpContent,_tmpDomain,_tmpTargetYear,_tmpTargetYearLabel,_tmpCreatedAt,_tmpLanguageTag,_tmpObstacle,_tmpEmotion,_tmpIfThenPlan,_tmpMissionId);
            _result.add(_item_1);
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
  public Object findById(final String id, final Continuation<? super JournalEntry> $completion) {
    final String _sql = "SELECT * FROM journal_entries WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<JournalEntry>() {
      @Override
      @Nullable
      public JournalEntry call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfDomain = CursorUtil.getColumnIndexOrThrow(_cursor, "domain");
          final int _cursorIndexOfTargetYear = CursorUtil.getColumnIndexOrThrow(_cursor, "targetYear");
          final int _cursorIndexOfTargetYearLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "targetYearLabel");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfLanguageTag = CursorUtil.getColumnIndexOrThrow(_cursor, "languageTag");
          final int _cursorIndexOfObstacle = CursorUtil.getColumnIndexOrThrow(_cursor, "obstacle");
          final int _cursorIndexOfEmotion = CursorUtil.getColumnIndexOrThrow(_cursor, "emotion");
          final int _cursorIndexOfIfThenPlan = CursorUtil.getColumnIndexOrThrow(_cursor, "ifThenPlan");
          final int _cursorIndexOfMissionId = CursorUtil.getColumnIndexOrThrow(_cursor, "missionId");
          final JournalEntry _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpDomain;
            _tmpDomain = _cursor.getString(_cursorIndexOfDomain);
            final int _tmpTargetYear;
            _tmpTargetYear = _cursor.getInt(_cursorIndexOfTargetYear);
            final String _tmpTargetYearLabel;
            _tmpTargetYearLabel = _cursor.getString(_cursorIndexOfTargetYearLabel);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpLanguageTag;
            _tmpLanguageTag = _cursor.getString(_cursorIndexOfLanguageTag);
            final String _tmpObstacle;
            if (_cursor.isNull(_cursorIndexOfObstacle)) {
              _tmpObstacle = null;
            } else {
              _tmpObstacle = _cursor.getString(_cursorIndexOfObstacle);
            }
            final String _tmpEmotion;
            if (_cursor.isNull(_cursorIndexOfEmotion)) {
              _tmpEmotion = null;
            } else {
              _tmpEmotion = _cursor.getString(_cursorIndexOfEmotion);
            }
            final String _tmpIfThenPlan;
            if (_cursor.isNull(_cursorIndexOfIfThenPlan)) {
              _tmpIfThenPlan = null;
            } else {
              _tmpIfThenPlan = _cursor.getString(_cursorIndexOfIfThenPlan);
            }
            final String _tmpMissionId;
            if (_cursor.isNull(_cursorIndexOfMissionId)) {
              _tmpMissionId = null;
            } else {
              _tmpMissionId = _cursor.getString(_cursorIndexOfMissionId);
            }
            _result = new JournalEntry(_tmpId,_tmpUserId,_tmpContent,_tmpDomain,_tmpTargetYear,_tmpTargetYearLabel,_tmpCreatedAt,_tmpLanguageTag,_tmpObstacle,_tmpEmotion,_tmpIfThenPlan,_tmpMissionId);
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
