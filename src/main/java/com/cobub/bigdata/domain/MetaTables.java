package com.cobub.bigdata.domain;

/**
 * Created by weifeng on 2015/8/12.
 */
public class MetaTables {

    final static public String COLUMN_FAMILY = "f";
    /**
     * Namespace prefix in HBase. Format: namespace:table . If there has no
     * namespace, leave it empty.
     */
    private static String nsPrefix = "razor:";

    /**
     * Set hbase namespace.
     *
     * @param nameSpace
     */
    public static void setNameSpace(String nameSpace) {
        nsPrefix = nameSpace + ":";
    }

    public static class ClientData {
        public final static String TABLE_NAME = nsPrefix + "clientdata";


        public class QUALIFIERS {
            final static public String VERSION = "version";
            final static public String OS_VERSION = "os_version";
            final static public String RESOLUTION = "resolution";
            final static public String DEVICE_NAME = "devicename";
            final static public String DEVICE_ID = "deviceid";
            final static public String PRODUCT_ID = "productid";
            final static public String CHANNEL_ID = "channelid";
            final static public String CARRIER = "carrier";
            final static public String NETWORK = "network";
            final static public String COUNTRY = "country";
            final static public String REGION = "region";
            final static public String CITY = "city";
            final static public String LOCAL_TIME = "localtime";
        }

    }


    public static class UsingLog {
        final static public String TABLE_NAME = nsPrefix + "usinglog";

    }


    public static class Event {
        final static public String TABLE_NAME = nsPrefix + "event";

    }


    public static class Error {
        final static public String TABLE_NAME = nsPrefix + "error";

    }


    public static class FullProductDevice {
        final static public String TABLE_NAME = nsPrefix + "F_Product_Device";
    }


    public static class SumStatistic {
        final static public String TABLE_NAME = nsPrefix + "Sum_Statistic";
    }


    public static class SumError {
        final static public String TABLE_NAME = nsPrefix + "sumerror";
    }


    public static class ErrorDetails {
        final static public String TABLE_NAME = nsPrefix + "error_details";
    }


    public static class FrequencyVersion {
        final static public String TABLE_NAME = nsPrefix + "frequencyversion";
    }


    public static class FrequencyChannel {
        final static public String TABLE_NAME = nsPrefix + "frequencychannel";
    }


    public static class FrequencyProduct {
        final static public String TABLE_NAME = nsPrefix + "frequencyproduct";
    }


    public static class MonthlyReserve {
        final static public String TABLE_NAME = nsPrefix + "MonthlyReserve";
    }


    public static class MonthlyReserveV {
        final static public String TABLE_NAME = nsPrefix + "MonthlyReserve_V";
    }


    public static class MonthlyReserveC {
        final static public String TABLE_NAME = nsPrefix + "MonthlyReserve_C";
    }


    public static class DailyReserve {
        final static public String TABLE_NAME = nsPrefix + "DailyReserve";
    }


    public static class DailyReserveV {
        final static public String TABLE_NAME = nsPrefix + "DailyReserve_V";
    }


    public static class DailyReserveC {
        final static public String TABLE_NAME = nsPrefix + "DailyReserve_C";
    }


    public static class WeeklyReserve {
        final static public String TABLE_NAME = nsPrefix + "WeeklyReserve";
    }


    public static class WeeklyReserveV {
        final static public String TABLE_NAME = nsPrefix + "WeeklyReserve_V";
    }


    public static class WeeklyReserveC {
        final static public String TABLE_NAME = nsPrefix + "WeeklyReserve_C";
    }


    public static class DurationProduct {
        final static public String TABLE_NAME = nsPrefix + "durationproduct";
    }


    public static class DurationVersion {
        final static public String TABLE_NAME = nsPrefix + "durationversion";
    }


    public static class DurationChannel {
        final static public String TABLE_NAME = nsPrefix + "durationchannel";
    }


    public static class AccessLevel {
        final static public String TABLE_NAME = nsPrefix + "accesslevel";
    }


    public static class SumEvent {
        final static public String TABLE_NAME = nsPrefix + "sumevent";
    }


    public static class MD5TitleTable {
        final static public String TABLE_NAME = nsPrefix + "MD5_TITLE_Table";

    }


    public static class MD5StacktraceTable {
        final static public String TABLE_NAME = nsPrefix + "MD5_STACKTRACE_Table";
    }


    public static class WeeklyActivity {
        final static public String TABLE_NAME = nsPrefix + "weeklyactivity";

    }


    public static class MonthlyActivity {
        final static public String TABLE_NAME = nsPrefix + "monthlyactivity";
    }


    public static class ModelManufacturer {
        final static public String TABLE_NAME = nsPrefix + "model_manufacturer";


        public static class QUALIFIERS {
            final static public String MANUFACTURER = "mf";
        }
    }


    public static class KeywordManufacturer {
        final static public String TABLE_NAME = nsPrefix + "keyword_manufacturer";


        public static class QUALIFIERS {
            final static public String MANUFACTURER = "mf";
        }
    }


    public static class ProductChannel {
        final static public String TABLE_NAME = nsPrefix + "product_channel";


        public static class QUALIFIERS {
            final static public String PRODUCT_CHANNEL = "pc";
        }
    }


    public static class Mccmnc {
        final static public String TABLE_NAME = nsPrefix + "mccmnc";


        public static class QUALIFIERS {
            final static public String NAME = "name";
        }
    }


    public static class Tag {
        final static public String TABLE_NAME = nsPrefix + "tag";
    }


    public static class AppInfo {
        final static public String TABLE_NAME = nsPrefix + "appinfo";
    }


    /**
     * Result HTable based on HTable sumappinfo.
     *
     * @author jianghe.cao
     */
    public static class SumAppInfo {
        final static public String TABLE_NAME = nsPrefix + "sumappinfo";
    }


    /**
     * Intermediate HTable based on HTable appinfo.
     *
     * @author jianghe.cao
     */
    public static class InterAppInfo {
        final static public String TABLE_NAME = nsPrefix + "interappinfo";
    }


    public static class RazorVersion {
        final static public String TABLE_NAME = nsPrefix + "razor_version";
    }


    public static class JunkClientdata {
        final static public String TABLE_NAME = nsPrefix + "junk_clientdata";
    }


    public static class JunkEvent {
        final static public String TABLE_NAME = nsPrefix + "junk_event";
    }


    public static class JunkUsinglog {
        final static public String TABLE_NAME = nsPrefix + "junk_usinglog";
    }


    public static class JunkError {
        final static public String TABLE_NAME = nsPrefix + "junk_error";
    }


    public static class JunkAppInfo {
        final static public String TABLE_NAME = nsPrefix + "junk_appinfo";
    }


    public static class JunkTag {
        final static public String TABLE_NAME = nsPrefix + "junk_tag";
    }


    public static class DimDurationSegment {
        final static public String TABLE_NAME = nsPrefix + "dimdurationsegment";
    }


    public static class DimSessionSegment {
        final static public String TABLE_NAME = nsPrefix + "dimsessionsegment";
    }

    public static class Participate_Degree {
        final static public String TABLE_NAME = nsPrefix + "participate_degree";
        final static public String COLUMN_F_RV = "RV";
        final static public String COLUMN_F_RO = "RO";
        final static public String COLUMN_F_BF = "BF";

        public class QUALIFIERS {
            final static public String D = "D";
            final static public String D7 = "D7";
            final static public String D14 = "D14";
            final static public String D30 = "D30";

            final static public String P = "P";
            final static public String P7 = "P7";
            final static public String P14 = "P14";
            final static public String P30 = "P30";

        }
    }
}
