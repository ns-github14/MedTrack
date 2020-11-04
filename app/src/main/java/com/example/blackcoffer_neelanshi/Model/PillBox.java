package com.example.blackcoffer_neelanshi.Model;

import android.content.Context;
import java.net.URISyntaxException;
import java.util.Collections;

/**
 * Created by CharlesPK3 on 4/3/15.
 * Is used to retrieve pills and alarms by other classes.
 * Has access to and can read and write to the database.
 */
public class PillBox {
    private DbHelper db;
    private static java.util.List<Long> tempIds; // Ids of the alarms to be deleted or edited
    private static String tempName; // Ids of the alarms to be deleted or edited

    public java.util.List<Long> getTempIds() { return Collections.unmodifiableList(tempIds); }

    public void setTempIds(java.util.List<Long> tempIds) { this.tempIds = tempIds; }

    public String getTempName() { return tempName; }

    public void setTempName(String tempName) { this.tempName = tempName; }

    public java.util.List<Pill> getPills(Context c) {
        db = new DbHelper(c);
        java.util.List<Pill> allPills = db.getAllPills();
        db.close();
        return allPills;
    }

    public long addPill(Context c, Pill pill) {
        db = new DbHelper(c);
        long pillId = db.createPill(pill);
        pill.setPillId(pillId);
        db.close();
        return pillId;
    }

    public Pill getPillByName(Context c, String pillName){
        db = new DbHelper(c);
        Pill wantedPill = db.getPillByName(pillName);
        db.close();
        return wantedPill;
    }

    public void addAlarm(Context c, Alarm alarm, Pill pill){
        db = new DbHelper(c);
        db.createAlarm(alarm, pill.getPillId());
        db.close();
    }

    public java.util.List<Alarm> getAlarms(Context c, int dayOfWeek) throws URISyntaxException {
        db = new DbHelper(c);
        java.util.List<Alarm> daysAlarms= db.getAlarmsByDay(dayOfWeek);
        db.close();
        Collections.sort(daysAlarms);
        return daysAlarms;
    }

    public java.util.List<Alarm> getAlarmByPill (Context c, String pillName) throws URISyntaxException {
        db = new DbHelper(c);
        java.util.List<Alarm> pillsAlarms = db.getAllAlarmsByPill(pillName);
        db.close();
        return pillsAlarms;
    }

    public boolean pillExist(Context c, String pillName) {
        db = new DbHelper(c);
        for(Pill pill: this.getPills(c)) {
            if(pill.getPillName().equals(pillName))
                return true;
        }
        return false;
    }

    public void deletePill(Context c, String pillName) throws URISyntaxException {
        db = new DbHelper(c);
        db.deletePill(pillName);
        db.close();
    }

    public void deleteAlarm(Context c, long alarmId) {
        db = new DbHelper(c);
        db.deleteAlarm(alarmId);
        db.close();
    }

    public void addToHistory(Context c, History h){
        db = new DbHelper(c);
        db.createHistory(h);
        db.close();
    }

    public java.util.List<History> getHistory (Context c){
        db = new DbHelper(c);
        java.util.List<History> history = db.getHistory();
        db.close();
        return history;
    }

    public Alarm getAlarmById(Context c, long alarm_id) throws URISyntaxException {
        db = new DbHelper(c);
        Alarm alarm = db.getAlarmById(alarm_id);
        db.close();
        return alarm;
    }

    public int getDayOfWeek(Context c, long alarm_id) throws URISyntaxException {
        db = new DbHelper(c);
        int getDayOfWeek = db.getDayOfWeek(alarm_id);
        db.close();
        return getDayOfWeek;
    }
}
