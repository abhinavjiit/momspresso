package com.mycity4kids.database;

import com.mycity4kids.dbtable.ActivititiesTable;
import com.mycity4kids.dbtable.AdvancedSearchTable;
import com.mycity4kids.dbtable.AgeGroupTable;
import com.mycity4kids.dbtable.CategoryListTable;
import com.mycity4kids.dbtable.CityTable;
import com.mycity4kids.dbtable.DateTable;
import com.mycity4kids.dbtable.ExternalCalendarTable;
import com.mycity4kids.dbtable.FilterTable;
import com.mycity4kids.dbtable.LocalityTable;
import com.mycity4kids.dbtable.SortByTable;
import com.mycity4kids.dbtable.SubCategoryTable;
import com.mycity4kids.dbtable.TableAdult;
import com.mycity4kids.dbtable.TableApiEvents;
import com.mycity4kids.dbtable.TableAppointmentData;
import com.mycity4kids.dbtable.TableAttendee;
import com.mycity4kids.dbtable.TableFamily;
import com.mycity4kids.dbtable.TableFile;
import com.mycity4kids.dbtable.TableKids;
import com.mycity4kids.dbtable.TableNotes;
import com.mycity4kids.dbtable.TableTaskData;
import com.mycity4kids.dbtable.TableTaskList;
import com.mycity4kids.dbtable.TableWhoToRemind;
import com.mycity4kids.dbtable.TaskCompletedTable;
import com.mycity4kids.dbtable.TaskTableAttendee;
import com.mycity4kids.dbtable.TaskTableFile;
import com.mycity4kids.dbtable.TaskTableNotes;
import com.mycity4kids.dbtable.TaskTableWhoToRemind;
import com.mycity4kids.dbtable.UserTable;

/**
 * DataBase constants
 */
public interface DBManifest {

    /**
     * Common constants for database and for all tables
     */
    String DB_NAME = "mycity4kids";
    int DB_VERSION = 9;

    String DB_COLUMN_ID = "id";
    int DB_INVALID_ID = -1;


    //table names...
    String[] TABLE_NAMES = new String[]{CategoryListTable.CATEOGTY_TABLE, SubCategoryTable.SUB_CATEOGTY_TABLE,
            AdvancedSearchTable.ADVANCED_SEARCH_TABLE, SortByTable.SORT_BY_TABLE, FilterTable.FILTER_TABLE,
            LocalityTable.LOCALITY_TABLE, CityTable.CITY_TABLE, UserTable.USER_TABLE, AgeGroupTable.AGE_GROUP_TABLE,
            ActivititiesTable.ACTIVITY_TABLE, DateTable.DATE_TABLE, TableAdult.ADULT_TABLE, TableKids.KIDS_TABLE,
            TableFamily.FAMILY_TABLE, TableAttendee.ATTTENDEE_TABLE, TableFile.FILE_TABLE, TableNotes.NOTES_TABLE,
            TableWhoToRemind.WHO_TO_REMIND_TABLE, TableAppointmentData.APPOINTMENT_TABLE, TableTaskData.TASK_TABLE,
            TableTaskList.TASK_LIST_TABLE, TaskTableAttendee.TASK_ATTTENDEE_TABLE, TaskTableNotes.TASK_NOTES_TABLE,
            TaskTableFile.TASK_FILE_TABLE, TaskTableWhoToRemind.TASK_WHO_TO_REMIND_TABLE, TaskCompletedTable.TASK_COMPLETE_TABLE, ExternalCalendarTable.EXTERNAL_CALENDAR_TABLE, TableApiEvents.API_EVENTS_TABLE};

    // Table create queries...
    String[] CREATE_QUERIES = new String[]{CategoryListTable.CREATE_CATEGORY_TABLE, SubCategoryTable.CREATE_SUB_CATEGORY_TABLE,
            AdvancedSearchTable.CREATE_ADVANCED_SEARCH_TABLE, SortByTable.CREATE_SORT_BY_TABLE,
            FilterTable.CREATE_FILTER_TABLE, LocalityTable.CREATE_LOCALITY_TABLE, CityTable.CREATE_CITY_TABLE,
            UserTable.CREATE_USER_TABLE, AgeGroupTable.CREATE_AGE_GROUP_TABLE, ActivititiesTable.CREATE_ACTIVITY_TABLE,
            DateTable.CREATE_DATE_TABLE, TableAdult.CREATE_ADULT_TABLE, TableKids.CREATE_KIDS_TABLE,
            TableFamily.CREATE_FAMILY_TABLE, TableAttendee.CREATE_ATTTENDEE_TABLE, TableWhoToRemind.CREATE_WHOTO_REMIND_TABLE,
            TableFile.CREATE_FILE_TABLE, TableNotes.CREATE_NOTES_TABLE, TableAppointmentData.CREATE_APPOINTMENT_TABLE,
            TableTaskData.CREATE_TASK_TABLE, TableTaskList.CREATE_TASK_LIST_TABLE, TaskTableAttendee.CREATE_TASK_ATTTENDEE_TABLE,
            TaskTableNotes.CREATE_TASK_NOTES_TABLE, TaskTableFile.CREATE_TASK_FILE_TABLE, TaskTableWhoToRemind.CREATE_TASK_WHOTO_REMIND_TABLE, TaskCompletedTable.CREATE_TASK_COMPLETE_TABLE, ExternalCalendarTable.CREATE_EXTERNAL_CALENDAR_TABLE, TableApiEvents.CREATE_API_EVENTS_TABLE};


    String[] DROP_QUERIES = new String[]{CategoryListTable.DROP_QUERY, SubCategoryTable.DROP_QUERY,
            AdvancedSearchTable.DROP_QUERY, SortByTable.DROP_QUERY,
            FilterTable.DROP_QUERY, LocalityTable.DROP_QUERY, CityTable.DROP_QUERY,
            UserTable.DROP_QUERY, AgeGroupTable.DROP_QUERY, ActivititiesTable.DROP_QUERY,
            DateTable.DROP_QUERY, TableAdult.DROP_QUERY, TableKids.DROP_QUERY,
            TableFamily.DROP_QUERY, TableAttendee.DROP_QUERY, TableWhoToRemind.DROP_QUERY,
            TableFile.DROP_QUERY, TableNotes.DROP_QUERY, TableAppointmentData.DROP_QUERY,
            TableTaskData.DROP_QUERY, TableTaskList.DROP_QUERY, TaskTableAttendee.DROP_QUERY,
            TaskTableNotes.DROP_QUERY, TaskTableFile.DROP_QUERY, TaskTableWhoToRemind.DROP_QUERY, TaskCompletedTable.DROP_QUERY, ExternalCalendarTable.DROP_QUERY, TableApiEvents.DROP_QUERY};


}

