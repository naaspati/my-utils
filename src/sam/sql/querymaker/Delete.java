package sam.sql.querymaker;

import java.util.function.UnaryOperator;

public class Delete extends QueryMakerBase {
    public Delete(StringBuilder sb, QueryMaker maker) {
        super(sb, maker, "DELETE FROM");
    }
    
    public Delete where(UnaryOperator<Where> w) {
        w.apply(where());
        return this;
    }
    public Delete from(String tableName) {
        appendAndSpace(tableName);
        return this;
    }
}
