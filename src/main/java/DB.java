import org.sql2o.Sql2o;

public class DB {
    private Sql2o sql2o;
    private static DB ourInstance = new DB();

    public static DB getInstance() {
        return ourInstance;
    }

    private DB() {
        sql2o = new Sql2o("jdbc:mysql://127.0.0.1:3306/mysql", "root", "468729");
    }
}
