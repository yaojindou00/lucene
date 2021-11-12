package com.hdsx.geohome.geocoding.vo;

/**
 * Created by jzh on 2017/6/24.
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @项目名称：lucense
 * @类名称：QueryResult
 * @类描述：结果集
 * @创建人：kehy
 * @创建时间：2016年9月15日 下午13:21
 * @version 1.0.0
 */
public class QueryResult implements Serializable {
    private static final long serialVersionUID = -572777973317877882L;
    private int count;
    private List result = new ArrayList();

    public QueryResult() {
    }

    public QueryResult(int count, List list) {
        this.count = count;
        this.result.addAll(list);
    }

    public List getResult() {
        return result;
    }

    public void setResult(List result) {
        this.result = result;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "QueryResult{" +
                "count=" + count +
                ", list=" + result +
                '}';
    }
}
