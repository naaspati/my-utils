package sam.sql.querymaker;

abstract class QueryMakerBase extends Appender {
    protected final StringBuilder sb;
    private final QueryMaker maker;

    protected QueryMakerBase(StringBuilder sb, QueryMaker maker, String start) {
        this(sb, maker, start, true);
    }
    protected QueryMakerBase(StringBuilder sb, String start) {
        this(sb, null, start, false);
    }
    protected QueryMakerBase(StringBuilder sb, QueryMaker maker, String start, boolean resetSize) {
        this.sb = sb;
        this.maker = maker;
        if(resetSize)
            sb.setLength(0);
        appendAndSpace(start);
    }
    protected Where where() {
       return new Where(sb);
    }
    public String build() {
        String s = sb.toString();
        maker.addBuilder(sb);
        return s;
    }
    @Override
    public StringBuilder sb() {
        return sb;
    }
}
