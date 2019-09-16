package com.example.witsdaily;

import java.text.ParseException;

public class bookableSession extends Session {
    public bookableSession(String venue, String repeatType, int repeatGap, String nextDate, String sessionType, int duration, String course, int numSessions, int slotGap, int slotDuration, String lastDate) throws ParseException {
        super(venue, repeatType, repeatGap, nextDate, sessionType, duration, course);
    }

    @Override
    public int compareTo(Session session) {
        if(dayVal == session.dayVal){
            return (int)(this.time.getTime() - session.time.getTime());
        }else {
            return this.dayVal - session.dayVal;
        }
    }
}
