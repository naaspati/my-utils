package sam.sql.querymaker;

import java.lang.ref.WeakReference;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class QueryMaker {
    private static final AtomicReference<WeakReference<QueryMaker>> instance = new AtomicReference<>();

    public static QueryMaker getInstance() {
        synchronized (instance) {
            QueryMaker qm = Optional.ofNullable(instance.get()).map(WeakReference::get).orElse(null);
            if (qm == null)
                instance.set(new WeakReference<>(qm = new QueryMaker()));
            return qm;            
        }
    }
    
    private QueryMaker() {}
    
  //TODO select
    public Select selectAll() {
        return new Select(this).all();
    }
    public Select select(String...columnNames) {
        return new Select(this).columns(columnNames);
    }
    public Select select(boolean quoted, String...columnNames) {
        return new Select(this).columns(columnNames, quoted);
    }
    public Select selectAllFrom(String tableName) {
        return selectAll().from(tableName);
    }
    
    //TODO update
    public Update update(String tableName) {
        return new Update(this, tableName);
    }
    
    //TODO insert
    public Insert insertInto(String tableName) {
        return new Insert(getBuilder(), this, tableName);
    }
    
    //TODO delete
    public Delete deleteFrom(String tableName) {
        return new Delete(getBuilder(), this).from(tableName);
    }
    
    private volatile boolean inUse = false;
    private StringBuilder builder = new StringBuilder();
    
    void addBuilder(StringBuilder sb) {
        if(sb == builder)
            inUse = false;
    }
    StringBuilder getBuilder() {
        if(inUse)
            return new StringBuilder();
        else {
            inUse = true;
            return builder;
        }
    }

}
