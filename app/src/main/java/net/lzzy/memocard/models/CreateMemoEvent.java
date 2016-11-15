package net.lzzy.memocard.models;

/**
 * Created by Administrator on 2016/6/1.
 * Create新事件
 */
public class CreateMemoEvent {
    private final String mid;

    public CreateMemoEvent(String mid) {
        this.mid = mid;
    }

    public String getMid() {
        return mid;
    }
}
