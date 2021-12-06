package sql_classes;

import java.sql.ResultSet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Sonaal on 25-08-2018.
 */

public class SQLQueryResult {
    ResultSet rs;
    AtomicInteger errorCode; // 1 == SQLError , 2 == Wifi Error

    public SQLQueryResult(ResultSet rs, AtomicInteger errorCode) {
        this.rs = rs;
        this.errorCode = errorCode;
    }

    public ResultSet getRs() {
        return rs;
    }

    public void setRs(ResultSet rs) {
        this.rs = rs;
    }

    public AtomicInteger getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(AtomicInteger errorCode) {
        this.errorCode = errorCode;
    }
}
