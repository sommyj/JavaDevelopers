package sommy.org.javadevelopers.model;

/**
 * Created by somto on 7/23/17.
 */

public class JavaDevelopers {
    private int total_count;
    private  boolean incomplete_results;
    private Items[] items;

    public int getTotal_count() {
        return total_count;
    }

    public boolean isIncomplete_results() {
        return incomplete_results;
    }

    public Items[] getItems() {
        return items;
    }
}
