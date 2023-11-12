package cc.corentin;
import cc.corentin.util.ConcurrentHashMapAutoCleaning;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        // create with a lifespan of 60s for all entries
        ConcurrentHashMapAutoCleaning<String,String> map = new ConcurrentHashMapAutoCleaning<>(60*1000);// 60*1000=60s
        // see the constructor for the different available options


        map.setCleanPeriod(60*60*1000);// Every hour the map is cleaned

        map.put("test1","test");
        assert "test".equals(map.get("test1")); // we can retrieve from the map
        Thread.sleep(60*1000);
        assert !("test".equals(map.get("test1"))); // if the time is exceeded, we can no longer retrieve it and it will be deleted during cleaning (period defined with setCleanPeriod)


        map.put("test1","test",2*60*1000);// we can specify a longer lifespan or ExtraLifeTimeAfterUseMillis at the time of addition

        map.close();// don't forget to close the map when no longer in use, closing is only necessary if we have set a cleanPeriod

        map = new ConcurrentHashMapAutoCleaning<>(60*1000,40*1000);// 60*1000=60s
        // it is possible to specify extraLifeTimeAfterUseMillis

        map.put("test1","test");
        Thread.sleep(60*1000);
        assert !("test".equals(map.get("test1"))); // same behavior, it is no longer accessible at the end of its lifespan

        map.put("test1","test");
        Thread.sleep(50*1000);
        map.getAndUpdateTimeSinceLastUse("test1");// still available
        Thread.sleep(10*1000); // total waiting time is 60s so the entry has exceeded its lifespan
        assert "test".equals(map.get("test1")); // the object is still available because it was used less than 40s ago (extraLifeTimeAfterUseMillis)


        map.cleanBlocking();// if we haven't set a cleanPeriod, it is possible to do a manual cleaning


        map.isEmpty();
        map.size();// it is preferable not to use these 2 functions as they are relatively slow since they have to traverse the entire map

        map.close();

    }
}