package logic.utils;

import java.util.List;

public interface Buffer {

    public void putM(List<Object> objects) throws InterruptedException;

    public List<Object> takeM(int numberOfItems) throws InterruptedException;

    public String type();
}
