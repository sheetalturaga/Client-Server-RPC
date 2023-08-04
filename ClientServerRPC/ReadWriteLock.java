package ClientServerRPC;

/**
 * Read and Writing lock for the threads in the server
 */
public class ReadWriteLock {
  private int threadReaders = 0;
  private int threadWriters = 0;
  private int reqs = 0;

  public void lockingReadFunction() throws InterruptedException {
    while (threadWriters > 0 || reqs > 0) {
      wait();
    }
    threadReaders++;
  }

  public void unlockingReadFunction() {
    threadReaders--;
    notifyAll();
  }

  public void lockingWriteFunction() throws InterruptedException {
    reqs++;
    while(threadReaders > 0 || threadWriters > 0) {
      wait();
    }
    reqs--;
    threadWriters++;
  }

  public void unlockingWriteFunction() {
    threadWriters--;
    notifyAll();
  }

}
