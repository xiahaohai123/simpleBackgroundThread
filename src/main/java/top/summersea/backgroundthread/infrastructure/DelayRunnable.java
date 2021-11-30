package top.summersea.backgroundthread.infrastructure;

import java.util.Objects;

public abstract class DelayRunnable implements Runnable {

    /** 任务标识 */
    private final String identifier;

    /**
     * 构造器
     * @param identifier 任务标识
     */
    public DelayRunnable(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DelayRunnable that = (DelayRunnable) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }
}
