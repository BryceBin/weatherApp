package com.example.bin.weatherapp;

/**
 * @Author: Bhy
 * @Date: 2018/12/9
 */
public class weatherDbSchema {
    //天气预测表
    public static final class forecastTable{
        public static final String NAME = "forecast";
        public static final class Cols{
            public static final String ID = "id";
            public static final String CON_CODE = "cond_code_d";//白天天气状况代码
            public static final String COND_TXT = "cond_txt_d";//白天天气状况描述
            public static final String DATE = "date";//预报日期
            public static final String HUM = "hum";//相对湿度
            public static final String PCPN = "pcpn";//降水量
            public static final String POP = "pop";//降水概率
            public static final String PRES = "pres";//大气压强
            public static final String TMP_MAX = "tmp_max";//最高温度
            public static final String TMP_MIN = "tmp_min";//最低温度
            public static final String UV_INDEX = "uv_index";//紫外线强度
            public static final String VIS = "vis";//能见度
            public static final String WIND_SC = "wind_sc";//风力
            public static final String WIND_DIR = "wind_dir";//风向
        }
    }

    //实时天气表
    public static final class realTimeTable{
        public static final String NAME = "realTime";
        public static final class Cols{
            public static final String CLOUD = "could";//云量
            public static final String CON_CODE = "cond_code";//天气状况代码
            public static final String COND_TXT = "cond_txt";//天气状况描述
            public static final String HUM = "hum";//相对湿度
            public static final String PCPN = "pcpn";//降水量
            public static final String PRES = "pres";//大气压强
            public static final String TMP = "tmp";//温度
            public static final String VIS = "vis";//能见度
            public static final String WIND_SC = "wind_sc";//风力
            public static final String WIND_DIR = "wind_dir";//风向
        }
    }
}
