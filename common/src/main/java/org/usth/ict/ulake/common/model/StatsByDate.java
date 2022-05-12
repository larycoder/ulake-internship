package org.usth.ict.ulake.common.model;

import java.sql.Date;

/**
 * A simple non-entity class for statistics by day
 */
public class StatsByDate {
    private Date date;
    private int count;

    public StatsByDate(Date date, int count) {
        this.date = date;
        this.count = count;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
