package sam.manga.newsamrock.chapters;

import sam.manga.newsamrock.SamrockDB;
import sam.sql.querymaker.QueryMaker;

abstract class Common {
    final SamrockDB db;

    Common(SamrockDB db) {
        this.db = db;
    }
    public static QueryMaker qm() {
       return QueryMaker.getInstance();
    }

}
