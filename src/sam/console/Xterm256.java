package sam.console;

public interface Xterm256 {
    /**
    * colorId: 0<br>
    * name: Black<br>
    * hexString: #000000<br>
    * rgb: (r=0, b=0, g=0)<br>
    * hsl: (s=0, h=0, l=0)<br>
    * <div style="border:1px solid black;width:80px;height:30px;background-color:#000000"></div><br/><br/>*/
    short BLACK = 0;
    /**
    * colorId: 1<br>
    * name: Maroon<br>
    * hexString: #800000<br>
    * rgb: (r=128, b=0, g=0)<br>
    * hsl: (s=100, h=0, l=25)<br>
    * <div style="border:1px solid black;width:80px;height:30px;background-color:#800000"></div><br/><br/>*/
    short MAROON = 1;
    /**
    * colorId: 2<br>
    * name: Green<br>
    * hexString: #008000<br>
    * rgb: (r=0, b=0, g=128)<br>
    * hsl: (s=100, h=120, l=25)<br>
    * <div style="border:1px solid black;width:80px;height:30px;background-color:#008000"></div><br/><br/>*/
    short GREEN = 2;
    /**
    * colorId: 3<br>
    * name: Olive<br>
    * hexString: #808000<br>
    * rgb: (r=128, b=0, g=128)<br>
    * hsl: (s=100, h=60, l=25)<br>
    * <div style="border:1px solid black;width:80px;height:30px;background-color:#808000"></div><br/><br/>*/
    short OLIVE = 3;
    /**
    * colorId: 4<br>
    * name: Navy<br>
    * hexString: #000080<br>
    * rgb: (r=0, b=128, g=0)<br>
    * hsl: (s=100, h=240, l=25)<br>
    * <div style="border:1px solid black;width:80px;height:30px;background-color:#000080"></div><br/><br/>*/
    short NAVY = 4;
    /**
    * colorId: 5<br>
    * name: Purple<br>
    * hexString: #800080<br>
    * rgb: (r=128, b=128, g=0)<br>
    * hsl: (s=100, h=300, l=25)<br>
    * <div style="border:1px solid black;width:80px;height:30px;background-color:#800080"></div><br/><br/>*/
    short PURPLE = 5;
    /**
    * colorId: 6<br>
    * name: Teal<br>
    * hexString: #008080<br>
    * rgb: (r=0, b=128, g=128)<br>
    * hsl: (s=100, h=180, l=25)<br>
    * <div style="border:1px solid black;width:80px;height:30px;background-color:#008080"></div><br/><br/>*/
    short TEAL = 6;
    /**
    * colorId: 7<br>
    * name: Silver<br>
    * hexString: #c0c0c0<br>
    * rgb: (r=192, b=192, g=192)<br>
    * hsl: (s=0, h=0, l=75)<br>
    * <div style="border:1px solid black;width:80px;height:30px;background-color:#c0c0c0"></div><br/><br/>*/
    short SILVER = 7;
    /**
    * colorId: 8<br>
    * name: Grey<br>
    * hexString: #808080<br>
    * rgb: (r=128, b=128, g=128)<br>
    * hsl: (s=0, h=0, l=50)<br>
    * <div style="border:1px solid black;width:80px;height:30px;background-color:#808080"></div><br/><br/>*/
    short GREY = 8;
    /**
    * colorId: 9<br>
    * name: Red<br>
    * hexString: #ff0000<br>
    * rgb: (r=255, b=0, g=0)<br>
    * hsl: (s=100, h=0, l=50)<br>
    * <div style="border:1px solid black;width:80px;height:30px;background-color:#ff0000"></div><br/><br/>*/
    short RED = 9;
    /**
    * colorId: 10<br>
    * name: Lime<br>
    * hexString: #00ff00<br>
    * rgb: (r=0, b=0, g=255)<br>
    * hsl: (s=100, h=120, l=50)<br>
    * <div style="border:1px solid black;width:80px;height:30px;background-color:#00ff00"></div><br/><br/>*/
    short LIME = 10;
    /**
    * colorId: 11<br>
    * name: Yellow<br>
    * hexString: #ffff00<br>
    * rgb: (r=255, b=0, g=255)<br>
    * hsl: (s=100, h=60, l=50)<br>
    * <div style="border:1px solid black;width:80px;height:30px;background-color:#ffff00"></div><br/><br/>*/
    short YELLOW = 11;
    /**
    * colorId: 12<br>
    * name: Blue<br>
    * hexString: #0000ff<br>
    * rgb: (r=0, b=255, g=0)<br>
    * hsl: (s=100, h=240, l=50)<br>
    * <div style="border:1px solid black;width:80px;height:30px;background-color:#0000ff"></div><br/><br/>*/
    short BLUE = 12;
    /**
    * colorId: 13<br>
    * name: Fuchsia<br>
    * hexString: #ff00ff<br>
    * rgb: (r=255, b=255, g=0)<br>
    * hsl: (s=100, h=300, l=50)<br>
    * <div style="border:1px solid black;width:80px;height:30px;background-color:#ff00ff"></div><br/><br/>*/
    short FUCHSIA = 13;
    /**
    * colorId: 14<br>
    * name: Aqua<br>
    * hexString: #00ffff<br>
    * rgb: (r=0, b=255, g=255)<br>
    * hsl: (s=100, h=180, l=50)<br>
    * <div style="border:1px solid black;width:80px;height:30px;background-color:#00ffff"></div><br/><br/>*/
    short AQUA = 14;
    /**
    * colorId: 15<br>
    * name: White<br>
    * hexString: #ffffff<br>
    * rgb: (r=255, b=255, g=255)<br>
    * hsl: (s=0, h=0, l=100)<br>
    * <div style="border:1px solid black;width:80px;height:30px;background-color:#ffffff"></div><br/><br/>*/
    short WHITE = 15;



/* ######################################################
######################## aqua ########################
###################################################### */

/**
* colorId: 86<br>
* name: Aquamarine1<br>
* hexString: #5fffd7<br>
* rgb: (r=95, b=215, g=255)<br>
* hsl: (s=100, h=165, l=68)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#5fffd7"></div><br/><br/>*/
short AQUAMARINE_1_2 = 86;

/**
* colorId: 122<br>
* name: Aquamarine1<br>
* hexString: #87ffd7<br>
* rgb: (r=135, b=215, g=255)<br>
* hsl: (s=100, h=160, l=76)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#87ffd7"></div><br/><br/>*/
short AQUAMARINE_1_3 = 122;

/**
* colorId: 79<br>
* name: Aquamarine3<br>
* hexString: #5fd7af<br>
* rgb: (r=95, b=175, g=215)<br>
* hsl: (s=60, h=160, l=60)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#5fd7af"></div><br/><br/>*/
short AQUAMARINE_3 = 79;



/* ######################################################
######################## blue ########################
###################################################### */

/**
* colorId: 21<br>
* name: Blue1<br>
* hexString: #0000ff<br>
* rgb: (r=0, b=255, g=0)<br>
* hsl: (s=100, h=240, l=50)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#0000ff"></div><br/><br/>*/
short BLUE_1 = 21;

/**
* colorId: 19<br>
* name: Blue3<br>
* hexString: #0000af<br>
* rgb: (r=0, b=175, g=0)<br>
* hsl: (s=100, h=240, l=34)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#0000af"></div><br/><br/>*/
short BLUE_3_2 = 19;

/**
* colorId: 20<br>
* name: Blue3<br>
* hexString: #0000d7<br>
* rgb: (r=0, b=215, g=0)<br>
* hsl: (s=100, h=240, l=42)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#0000d7"></div><br/><br/>*/
short BLUE_3_3 = 20;

/**
* colorId: 57<br>
* name: BlueViolet<br>
* hexString: #5f00ff<br>
* rgb: (r=95, b=255, g=0)<br>
* hsl: (s=100, h=262.352941176471, l=50)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#5f00ff"></div><br/><br/>*/
short BLUEVIOLET = 57;

/**
* colorId: 72<br>
* name: CadetBlue<br>
* hexString: #5faf87<br>
* rgb: (r=95, b=135, g=175)<br>
* hsl: (s=33, h=150, l=52)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#5faf87"></div><br/><br/>*/
short CADETBLUE = 72;

/**
* colorId: 73<br>
* name: CadetBlue<br>
* hexString: #5fafaf<br>
* rgb: (r=95, b=175, g=175)<br>
* hsl: (s=33, h=180, l=52)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#5fafaf"></div><br/><br/>*/
short CADETBLUE_1 = 73;

/**
* colorId: 69<br>
* name: CornflowerBlue<br>
* hexString: #5f87ff<br>
* rgb: (r=95, b=255, g=135)<br>
* hsl: (s=100, h=225, l=68)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#5f87ff"></div><br/><br/>*/
short CORNFLOWERBLUE = 69;

/**
* colorId: 18<br>
* name: DarkBlue<br>
* hexString: #000087<br>
* rgb: (r=0, b=135, g=0)<br>
* hsl: (s=100, h=240, l=26)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#000087"></div><br/><br/>*/
short DARKBLUE = 18;

/**
* colorId: 39<br>
* name: DeepSkyBlue1<br>
* hexString: #00afff<br>
* rgb: (r=0, b=255, g=175)<br>
* hsl: (s=100, h=198.823529411765, l=50)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#00afff"></div><br/><br/>*/
short DEEPSKYBLUE_1 = 39;

/**
* colorId: 38<br>
* name: DeepSkyBlue2<br>
* hexString: #00afd7<br>
* rgb: (r=0, b=215, g=175)<br>
* hsl: (s=100, h=191.162790697674, l=42)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#00afd7"></div><br/><br/>*/
short DEEPSKYBLUE_2 = 38;

/**
* colorId: 31<br>
* name: DeepSkyBlue3<br>
* hexString: #0087af<br>
* rgb: (r=0, b=175, g=135)<br>
* hsl: (s=100, h=193.714285714286, l=34)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#0087af"></div><br/><br/>*/
short DEEPSKYBLUE_3_2 = 31;

/**
* colorId: 32<br>
* name: DeepSkyBlue3<br>
* hexString: #0087d7<br>
* rgb: (r=0, b=215, g=135)<br>
* hsl: (s=100, h=202.325581395349, l=42)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#0087d7"></div><br/><br/>*/
short DEEPSKYBLUE_3_3 = 32;

/**
* colorId: 23<br>
* name: DeepSkyBlue4<br>
* hexString: #005f5f<br>
* rgb: (r=0, b=95, g=95)<br>
* hsl: (s=100, h=180, l=18)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#005f5f"></div><br/><br/>*/
short DEEPSKYBLUE_4 = 23;

/**
* colorId: 24<br>
* name: DeepSkyBlue4<br>
* hexString: #005f87<br>
* rgb: (r=0, b=135, g=95)<br>
* hsl: (s=100, h=197.777777777778, l=26)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#005f87"></div><br/><br/>*/
short DEEPSKYBLUE_4_2 = 24;

/**
* colorId: 25<br>
* name: DeepSkyBlue4<br>
* hexString: #005faf<br>
* rgb: (r=0, b=175, g=95)<br>
* hsl: (s=100, h=207.428571428571, l=34)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#005faf"></div><br/><br/>*/
short DEEPSKYBLUE_4_3 = 25;

/**
* colorId: 33<br>
* name: DodgerBlue1<br>
* hexString: #0087ff<br>
* rgb: (r=0, b=255, g=135)<br>
* hsl: (s=100, h=208.235294117647, l=50)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#0087ff"></div><br/><br/>*/
short DODGERBLUE_1 = 33;

/**
* colorId: 27<br>
* name: DodgerBlue2<br>
* hexString: #005fff<br>
* rgb: (r=0, b=255, g=95)<br>
* hsl: (s=100, h=217.647058823529, l=50)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#005fff"></div><br/><br/>*/
short DODGERBLUE_2 = 27;

/**
* colorId: 26<br>
* name: DodgerBlue3<br>
* hexString: #005fd7<br>
* rgb: (r=0, b=215, g=95)<br>
* hsl: (s=100, h=213.488372093023, l=42)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#005fd7"></div><br/><br/>*/
short DODGERBLUE_3 = 26;

/**
* colorId: 153<br>
* name: LightSkyBlue1<br>
* hexString: #afd7ff<br>
* rgb: (r=175, b=255, g=215)<br>
* hsl: (s=100, h=210, l=84)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#afd7ff"></div><br/><br/>*/
short LIGHTSKYBLUE_1 = 153;

/**
* colorId: 109<br>
* name: LightSkyBlue3<br>
* hexString: #87afaf<br>
* rgb: (r=135, b=175, g=175)<br>
* hsl: (s=20, h=180, l=60)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#87afaf"></div><br/><br/>*/
short LIGHTSKYBLUE_3 = 109;

/**
* colorId: 110<br>
* name: LightSkyBlue3<br>
* hexString: #87afd7<br>
* rgb: (r=135, b=215, g=175)<br>
* hsl: (s=50, h=210, l=68)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#87afd7"></div><br/><br/>*/
short LIGHTSKYBLUE_3_2 = 110;

/**
* colorId: 105<br>
* name: LightSlateBlue<br>
* hexString: #8787ff<br>
* rgb: (r=135, b=255, g=135)<br>
* hsl: (s=100, h=240, l=76)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#8787ff"></div><br/><br/>*/
short LIGHTSLATEBLUE = 105;

/**
* colorId: 147<br>
* name: LightSteelBlue<br>
* hexString: #afafff<br>
* rgb: (r=175, b=255, g=175)<br>
* hsl: (s=100, h=240, l=84)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#afafff"></div><br/><br/>*/
short LIGHTSTEELBLUE = 147;

/**
* colorId: 189<br>
* name: LightSteelBlue1<br>
* hexString: #d7d7ff<br>
* rgb: (r=215, b=255, g=215)<br>
* hsl: (s=100, h=240, l=92)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#d7d7ff"></div><br/><br/>*/
short LIGHTSTEELBLUE_1 = 189;

/**
* colorId: 146<br>
* name: LightSteelBlue3<br>
* hexString: #afafd7<br>
* rgb: (r=175, b=215, g=175)<br>
* hsl: (s=33, h=240, l=76)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#afafd7"></div><br/><br/>*/
short LIGHTSTEELBLUE_3 = 146;

/**
* colorId: 63<br>
* name: RoyalBlue1<br>
* hexString: #5f5fff<br>
* rgb: (r=95, b=255, g=95)<br>
* hsl: (s=100, h=240, l=68)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#5f5fff"></div><br/><br/>*/
short ROYALBLUE_1 = 63;

/**
* colorId: 117<br>
* name: SkyBlue1<br>
* hexString: #87d7ff<br>
* rgb: (r=135, b=255, g=215)<br>
* hsl: (s=100, h=200, l=76)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#87d7ff"></div><br/><br/>*/
short SKYBLUE_1 = 117;

/**
* colorId: 111<br>
* name: SkyBlue2<br>
* hexString: #87afff<br>
* rgb: (r=135, b=255, g=175)<br>
* hsl: (s=100, h=220, l=76)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#87afff"></div><br/><br/>*/
short SKYBLUE_2 = 111;

/**
* colorId: 74<br>
* name: SkyBlue3<br>
* hexString: #5fafd7<br>
* rgb: (r=95, b=215, g=175)<br>
* hsl: (s=60, h=200, l=60)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#5fafd7"></div><br/><br/>*/
short SKYBLUE_3 = 74;

/**
* colorId: 99<br>
* name: SlateBlue1<br>
* hexString: #875fff<br>
* rgb: (r=135, b=255, g=95)<br>
* hsl: (s=100, h=255, l=68)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#875fff"></div><br/><br/>*/
short SLATEBLUE_1 = 99;

/**
* colorId: 61<br>
* name: SlateBlue3<br>
* hexString: #5f5faf<br>
* rgb: (r=95, b=175, g=95)<br>
* hsl: (s=33, h=240, l=52)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#5f5faf"></div><br/><br/>*/
short SLATEBLUE_3 = 61;

/**
* colorId: 62<br>
* name: SlateBlue3<br>
* hexString: #5f5fd7<br>
* rgb: (r=95, b=215, g=95)<br>
* hsl: (s=60, h=240, l=60)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#5f5fd7"></div><br/><br/>*/
short SLATEBLUE_3_2 = 62;

/**
* colorId: 67<br>
* name: SteelBlue<br>
* hexString: #5f87af<br>
* rgb: (r=95, b=175, g=135)<br>
* hsl: (s=33, h=210, l=52)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#5f87af"></div><br/><br/>*/
short STEELBLUE = 67;

/**
* colorId: 75<br>
* name: SteelBlue1<br>
* hexString: #5fafff<br>
* rgb: (r=95, b=255, g=175)<br>
* hsl: (s=100, h=210, l=68)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#5fafff"></div><br/><br/>*/
short STEELBLUE_1 = 75;

/**
* colorId: 81<br>
* name: SteelBlue1<br>
* hexString: #5fd7ff<br>
* rgb: (r=95, b=255, g=215)<br>
* hsl: (s=100, h=195, l=68)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#5fd7ff"></div><br/><br/>*/
short STEELBLUE_1_2 = 81;

/**
* colorId: 68<br>
* name: SteelBlue3<br>
* hexString: #5f87d7<br>
* rgb: (r=95, b=215, g=135)<br>
* hsl: (s=60, h=220, l=60)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#5f87d7"></div><br/><br/>*/
short STEELBLUE_3 = 68;



/* #######################################################
######################## green ########################
####################################################### */

/**
* colorId: 22<br>
* name: DarkGreen<br>
* hexString: #005f00<br>
* rgb: (r=0, b=0, g=95)<br>
* hsl: (s=100, h=120, l=18)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#005f00"></div><br/><br/>*/
short DARKGREEN = 22;

/**
* colorId: 191<br>
* name: DarkOliveGreen1<br>
* hexString: #d7ff5f<br>
* rgb: (r=215, b=95, g=255)<br>
* hsl: (s=100, h=75, l=68)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#d7ff5f"></div><br/><br/>*/
short DARKOLIVEGREEN_1 = 191;

/**
* colorId: 192<br>
* name: DarkOliveGreen1<br>
* hexString: #d7ff87<br>
* rgb: (r=215, b=135, g=255)<br>
* hsl: (s=100, h=80, l=76)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#d7ff87"></div><br/><br/>*/
short DARKOLIVEGREEN_1_2 = 192;

/**
* colorId: 155<br>
* name: DarkOliveGreen2<br>
* hexString: #afff5f<br>
* rgb: (r=175, b=95, g=255)<br>
* hsl: (s=100, h=90, l=68)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#afff5f"></div><br/><br/>*/
short DARKOLIVEGREEN_2 = 155;

/**
* colorId: 107<br>
* name: DarkOliveGreen3<br>
* hexString: #87af5f<br>
* rgb: (r=135, b=95, g=175)<br>
* hsl: (s=33, h=90, l=52)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#87af5f"></div><br/><br/>*/
short DARKOLIVEGREEN_3 = 107;

/**
* colorId: 113<br>
* name: DarkOliveGreen3<br>
* hexString: #87d75f<br>
* rgb: (r=135, b=95, g=215)<br>
* hsl: (s=60, h=100, l=60)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#87d75f"></div><br/><br/>*/
short DARKOLIVEGREEN_3_2 = 113;

/**
* colorId: 149<br>
* name: DarkOliveGreen3<br>
* hexString: #afd75f<br>
* rgb: (r=175, b=95, g=215)<br>
* hsl: (s=60, h=80, l=60)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#afd75f"></div><br/><br/>*/
short DARKOLIVEGREEN_3_3 = 149;

/**
* colorId: 108<br>
* name: DarkSeaGreen<br>
* hexString: #87af87<br>
* rgb: (r=135, b=135, g=175)<br>
* hsl: (s=20, h=120, l=60)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#87af87"></div><br/><br/>*/
short DARKSEAGREEN = 108;

/**
* colorId: 158<br>
* name: DarkSeaGreen1<br>
* hexString: #afffd7<br>
* rgb: (r=175, b=215, g=255)<br>
* hsl: (s=100, h=150, l=84)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#afffd7"></div><br/><br/>*/
short DARKSEAGREEN_1 = 158;

/**
* colorId: 193<br>
* name: DarkSeaGreen1<br>
* hexString: #d7ffaf<br>
* rgb: (r=215, b=175, g=255)<br>
* hsl: (s=100, h=90, l=84)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#d7ffaf"></div><br/><br/>*/
short DARKSEAGREEN_1_2 = 193;

/**
* colorId: 151<br>
* name: DarkSeaGreen2<br>
* hexString: #afd7af<br>
* rgb: (r=175, b=175, g=215)<br>
* hsl: (s=33, h=120, l=76)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#afd7af"></div><br/><br/>*/
short DARKSEAGREEN_2 = 151;

/**
* colorId: 157<br>
* name: DarkSeaGreen2<br>
* hexString: #afffaf<br>
* rgb: (r=175, b=175, g=255)<br>
* hsl: (s=100, h=120, l=84)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#afffaf"></div><br/><br/>*/
short DARKSEAGREEN_2_2 = 157;

/**
* colorId: 115<br>
* name: DarkSeaGreen3<br>
* hexString: #87d7af<br>
* rgb: (r=135, b=175, g=215)<br>
* hsl: (s=50, h=150, l=68)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#87d7af"></div><br/><br/>*/
short DARKSEAGREEN_3 = 115;

/**
* colorId: 150<br>
* name: DarkSeaGreen3<br>
* hexString: #afd787<br>
* rgb: (r=175, b=135, g=215)<br>
* hsl: (s=50, h=90, l=68)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#afd787"></div><br/><br/>*/
short DARKSEAGREEN_3_2 = 150;

/**
* colorId: 65<br>
* name: DarkSeaGreen4<br>
* hexString: #5f875f<br>
* rgb: (r=95, b=95, g=135)<br>
* hsl: (s=17, h=120, l=45)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#5f875f"></div><br/><br/>*/
short DARKSEAGREEN_4 = 65;

/**
* colorId: 71<br>
* name: DarkSeaGreen4<br>
* hexString: #5faf5f<br>
* rgb: (r=95, b=95, g=175)<br>
* hsl: (s=33, h=120, l=52)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#5faf5f"></div><br/><br/>*/
short DARKSEAGREEN_4_2 = 71;

/**
* colorId: 46<br>
* name: Green1<br>
* hexString: #00ff00<br>
* rgb: (r=0, b=0, g=255)<br>
* hsl: (s=100, h=120, l=50)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#00ff00"></div><br/><br/>*/
short GREEN_1 = 46;

/**
* colorId: 34<br>
* name: Green3<br>
* hexString: #00af00<br>
* rgb: (r=0, b=0, g=175)<br>
* hsl: (s=100, h=120, l=34)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#00af00"></div><br/><br/>*/
short GREEN_3 = 34;

/**
* colorId: 40<br>
* name: Green3<br>
* hexString: #00d700<br>
* rgb: (r=0, b=0, g=215)<br>
* hsl: (s=100, h=120, l=42)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#00d700"></div><br/><br/>*/
short GREEN_3_2 = 40;

/**
* colorId: 28<br>
* name: Green4<br>
* hexString: #008700<br>
* rgb: (r=0, b=0, g=135)<br>
* hsl: (s=100, h=120, l=26)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#008700"></div><br/><br/>*/
short GREEN_4 = 28;

/**
* colorId: 154<br>
* name: GreenYellow<br>
* hexString: #afff00<br>
* rgb: (r=175, b=0, g=255)<br>
* hsl: (s=100, h=78.8235294117647, l=50)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#afff00"></div><br/><br/>*/
short GREENYELLOW = 154;

/**
* colorId: 119<br>
* name: LightGreen<br>
* hexString: #87ff5f<br>
* rgb: (r=135, b=95, g=255)<br>
* hsl: (s=100, h=105, l=68)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#87ff5f"></div><br/><br/>*/
short LIGHTGREEN = 119;

/**
* colorId: 120<br>
* name: LightGreen<br>
* hexString: #87ff87<br>
* rgb: (r=135, b=135, g=255)<br>
* hsl: (s=100, h=120, l=76)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#87ff87"></div><br/><br/>*/
short LIGHTGREEN_2 = 120;

/**
* colorId: 37<br>
* name: LightSeaGreen<br>
* hexString: #00afaf<br>
* rgb: (r=0, b=175, g=175)<br>
* hsl: (s=100, h=180, l=34)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#00afaf"></div><br/><br/>*/
short LIGHTSEAGREEN = 37;

/**
* colorId: 49<br>
* name: MediumSpringGreen<br>
* hexString: #00ffaf<br>
* rgb: (r=0, b=175, g=255)<br>
* hsl: (s=100, h=161.176470588235, l=50)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#00ffaf"></div><br/><br/>*/
short MEDIUMSPRINGGREEN = 49;

/**
* colorId: 121<br>
* name: PaleGreen1<br>
* hexString: #87ffaf<br>
* rgb: (r=135, b=175, g=255)<br>
* hsl: (s=100, h=140, l=76)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#87ffaf"></div><br/><br/>*/
short PALEGREEN_1 = 121;

/**
* colorId: 156<br>
* name: PaleGreen1<br>
* hexString: #afff87<br>
* rgb: (r=175, b=135, g=255)<br>
* hsl: (s=100, h=100, l=76)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#afff87"></div><br/><br/>*/
short PALEGREEN_1_2 = 156;

/**
* colorId: 77<br>
* name: PaleGreen3<br>
* hexString: #5fd75f<br>
* rgb: (r=95, b=95, g=215)<br>
* hsl: (s=60, h=120, l=60)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#5fd75f"></div><br/><br/>*/
short PALEGREEN_3 = 77;

/**
* colorId: 114<br>
* name: PaleGreen3<br>
* hexString: #87d787<br>
* rgb: (r=135, b=135, g=215)<br>
* hsl: (s=50, h=120, l=68)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#87d787"></div><br/><br/>*/
short PALEGREEN_3_2 = 114;

/**
* colorId: 84<br>
* name: SeaGreen1<br>
* hexString: #5fff87<br>
* rgb: (r=95, b=135, g=255)<br>
* hsl: (s=100, h=135, l=68)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#5fff87"></div><br/><br/>*/
short SEAGREEN_1 = 84;

/**
* colorId: 85<br>
* name: SeaGreen1<br>
* hexString: #5fffaf<br>
* rgb: (r=95, b=175, g=255)<br>
* hsl: (s=100, h=150, l=68)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#5fffaf"></div><br/><br/>*/
short SEAGREEN_1_2 = 85;

/**
* colorId: 83<br>
* name: SeaGreen2<br>
* hexString: #5fff5f<br>
* rgb: (r=95, b=95, g=255)<br>
* hsl: (s=100, h=120, l=68)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#5fff5f"></div><br/><br/>*/
short SEAGREEN_2 = 83;

/**
* colorId: 78<br>
* name: SeaGreen3<br>
* hexString: #5fd787<br>
* rgb: (r=95, b=135, g=215)<br>
* hsl: (s=60, h=140, l=60)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#5fd787"></div><br/><br/>*/
short SEAGREEN_3 = 78;

/**
* colorId: 48<br>
* name: SpringGreen1<br>
* hexString: #00ff87<br>
* rgb: (r=0, b=135, g=255)<br>
* hsl: (s=100, h=151.764705882353, l=50)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#00ff87"></div><br/><br/>*/
short SPRINGGREEN_1 = 48;

/**
* colorId: 42<br>
* name: SpringGreen2<br>
* hexString: #00d787<br>
* rgb: (r=0, b=135, g=215)<br>
* hsl: (s=100, h=157.674418604651, l=42)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#00d787"></div><br/><br/>*/
short SPRINGGREEN_2 = 42;

/**
* colorId: 47<br>
* name: SpringGreen2<br>
* hexString: #00ff5f<br>
* rgb: (r=0, b=95, g=255)<br>
* hsl: (s=100, h=142.352941176471, l=50)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#00ff5f"></div><br/><br/>*/
short SPRINGGREEN_2_2 = 47;

/**
* colorId: 35<br>
* name: SpringGreen3<br>
* hexString: #00af5f<br>
* rgb: (r=0, b=95, g=175)<br>
* hsl: (s=100, h=152.571428571429, l=34)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#00af5f"></div><br/><br/>*/
short SPRINGGREEN_3 = 35;

/**
* colorId: 41<br>
* name: SpringGreen3<br>
* hexString: #00d75f<br>
* rgb: (r=0, b=95, g=215)<br>
* hsl: (s=100, h=146.511627906977, l=42)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#00d75f"></div><br/><br/>*/
short SPRINGGREEN_3_2 = 41;

/**
* colorId: 29<br>
* name: SpringGreen4<br>
* hexString: #00875f<br>
* rgb: (r=0, b=95, g=135)<br>
* hsl: (s=100, h=162.222222222222, l=26)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#00875f"></div><br/><br/>*/
short SPRINGGREEN_4 = 29;



/* ######################################################
######################## grey ########################
###################################################### */

/**
* colorId: 16<br>
* name: Grey0<br>
* hexString: #000000<br>
* rgb: (r=0, b=0, g=0)<br>
* hsl: (s=0, h=0, l=0)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#000000"></div><br/><br/>*/
short GREY_0 = 16;

/**
* colorId: 231<br>
* name: Grey100<br>
* hexString: #ffffff<br>
* rgb: (r=255, b=255, g=255)<br>
* hsl: (s=0, h=0, l=100)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#ffffff"></div><br/><br/>*/
short GREY_100 = 231;

/**
* colorId: 234<br>
* name: Grey11<br>
* hexString: #1c1c1c<br>
* rgb: (r=28, b=28, g=28)<br>
* hsl: (s=0, h=0, l=10)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#1c1c1c"></div><br/><br/>*/
short GREY_11 = 234;

/**
* colorId: 235<br>
* name: Grey15<br>
* hexString: #262626<br>
* rgb: (r=38, b=38, g=38)<br>
* hsl: (s=0, h=0, l=14)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#262626"></div><br/><br/>*/
short GREY_15 = 235;

/**
* colorId: 236<br>
* name: Grey19<br>
* hexString: #303030<br>
* rgb: (r=48, b=48, g=48)<br>
* hsl: (s=0, h=0, l=18)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#303030"></div><br/><br/>*/
short GREY_19 = 236;

/**
* colorId: 237<br>
* name: Grey23<br>
* hexString: #3a3a3a<br>
* rgb: (r=58, b=58, g=58)<br>
* hsl: (s=0, h=0, l=22)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#3a3a3a"></div><br/><br/>*/
short GREY_23 = 237;

/**
* colorId: 238<br>
* name: Grey27<br>
* hexString: #444444<br>
* rgb: (r=68, b=68, g=68)<br>
* hsl: (s=0, h=0, l=26)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#444444"></div><br/><br/>*/
short GREY_27 = 238;

/**
* colorId: 232<br>
* name: Grey3<br>
* hexString: #080808<br>
* rgb: (r=8, b=8, g=8)<br>
* hsl: (s=0, h=0, l=3)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#080808"></div><br/><br/>*/
short GREY_3 = 232;

/**
* colorId: 239<br>
* name: Grey30<br>
* hexString: #4e4e4e<br>
* rgb: (r=78, b=78, g=78)<br>
* hsl: (s=0, h=0, l=30)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#4e4e4e"></div><br/><br/>*/
short GREY_30 = 239;

/**
* colorId: 240<br>
* name: Grey35<br>
* hexString: #585858<br>
* rgb: (r=88, b=88, g=88)<br>
* hsl: (s=0, h=0, l=34)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#585858"></div><br/><br/>*/
short GREY_35 = 240;

/**
* colorId: 59<br>
* name: Grey37<br>
* hexString: #5f5f5f<br>
* rgb: (r=95, b=95, g=95)<br>
* hsl: (s=0, h=0, l=37)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#5f5f5f"></div><br/><br/>*/
short GREY_37 = 59;

/**
* colorId: 241<br>
* name: Grey39<br>
* hexString: #626262<br>
* rgb: (r=98, b=98, g=98)<br>
* hsl: (s=0, h=0, l=37)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#626262"></div><br/><br/>*/
short GREY_39 = 241;

/**
* colorId: 242<br>
* name: Grey42<br>
* hexString: #6c6c6c<br>
* rgb: (r=108, b=108, g=108)<br>
* hsl: (s=0, h=0, l=40)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#6c6c6c"></div><br/><br/>*/
short GREY_42 = 242;

/**
* colorId: 243<br>
* name: Grey46<br>
* hexString: #767676<br>
* rgb: (r=118, b=118, g=118)<br>
* hsl: (s=0, h=0, l=46)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#767676"></div><br/><br/>*/
short GREY_46 = 243;

/**
* colorId: 244<br>
* name: Grey50<br>
* hexString: #808080<br>
* rgb: (r=128, b=128, g=128)<br>
* hsl: (s=0, h=0, l=50)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#808080"></div><br/><br/>*/
short GREY_50 = 244;

/**
* colorId: 102<br>
* name: Grey53<br>
* hexString: #878787<br>
* rgb: (r=135, b=135, g=135)<br>
* hsl: (s=0, h=0, l=52)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#878787"></div><br/><br/>*/
short GREY_53 = 102;

/**
* colorId: 245<br>
* name: Grey54<br>
* hexString: #8a8a8a<br>
* rgb: (r=138, b=138, g=138)<br>
* hsl: (s=0, h=0, l=54)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#8a8a8a"></div><br/><br/>*/
short GREY_54 = 245;

/**
* colorId: 246<br>
* name: Grey58<br>
* hexString: #949494<br>
* rgb: (r=148, b=148, g=148)<br>
* hsl: (s=0, h=0, l=58)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#949494"></div><br/><br/>*/
short GREY_58 = 246;

/**
* colorId: 247<br>
* name: Grey62<br>
* hexString: #9e9e9e<br>
* rgb: (r=158, b=158, g=158)<br>
* hsl: (s=0, h=0, l=61)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#9e9e9e"></div><br/><br/>*/
short GREY_62 = 247;

/**
* colorId: 139<br>
* name: Grey63<br>
* hexString: #af87af<br>
* rgb: (r=175, b=175, g=135)<br>
* hsl: (s=20, h=300, l=60)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#af87af"></div><br/><br/>*/
short GREY_63 = 139;

/**
* colorId: 248<br>
* name: Grey66<br>
* hexString: #a8a8a8<br>
* rgb: (r=168, b=168, g=168)<br>
* hsl: (s=0, h=0, l=65)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#a8a8a8"></div><br/><br/>*/
short GREY_66 = 248;

/**
* colorId: 145<br>
* name: Grey69<br>
* hexString: #afafaf<br>
* rgb: (r=175, b=175, g=175)<br>
* hsl: (s=0, h=0, l=68)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#afafaf"></div><br/><br/>*/
short GREY_69 = 145;

/**
* colorId: 233<br>
* name: Grey7<br>
* hexString: #121212<br>
* rgb: (r=18, b=18, g=18)<br>
* hsl: (s=0, h=0, l=7)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#121212"></div><br/><br/>*/
short GREY_7 = 233;

/**
* colorId: 249<br>
* name: Grey70<br>
* hexString: #b2b2b2<br>
* rgb: (r=178, b=178, g=178)<br>
* hsl: (s=0, h=0, l=69)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#b2b2b2"></div><br/><br/>*/
short GREY_70 = 249;

/**
* colorId: 250<br>
* name: Grey74<br>
* hexString: #bcbcbc<br>
* rgb: (r=188, b=188, g=188)<br>
* hsl: (s=0, h=0, l=73)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#bcbcbc"></div><br/><br/>*/
short GREY_74 = 250;

/**
* colorId: 251<br>
* name: Grey78<br>
* hexString: #c6c6c6<br>
* rgb: (r=198, b=198, g=198)<br>
* hsl: (s=0, h=0, l=77)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#c6c6c6"></div><br/><br/>*/
short GREY_78 = 251;

/**
* colorId: 252<br>
* name: Grey82<br>
* hexString: #d0d0d0<br>
* rgb: (r=208, b=208, g=208)<br>
* hsl: (s=0, h=0, l=81)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#d0d0d0"></div><br/><br/>*/
short GREY_82 = 252;

/**
* colorId: 188<br>
* name: Grey84<br>
* hexString: #d7d7d7<br>
* rgb: (r=215, b=215, g=215)<br>
* hsl: (s=0, h=0, l=84)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#d7d7d7"></div><br/><br/>*/
short GREY_84 = 188;

/**
* colorId: 253<br>
* name: Grey85<br>
* hexString: #dadada<br>
* rgb: (r=218, b=218, g=218)<br>
* hsl: (s=0, h=0, l=85)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#dadada"></div><br/><br/>*/
short GREY_85 = 253;

/**
* colorId: 254<br>
* name: Grey89<br>
* hexString: #e4e4e4<br>
* rgb: (r=228, b=228, g=228)<br>
* hsl: (s=0, h=0, l=89)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#e4e4e4"></div><br/><br/>*/
short GREY_89 = 254;

/**
* colorId: 255<br>
* name: Grey93<br>
* hexString: #eeeeee<br>
* rgb: (r=238, b=238, g=238)<br>
* hsl: (s=0, h=0, l=93)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#eeeeee"></div><br/><br/>*/
short GREY_93 = 255;

/**
* colorId: 103<br>
* name: LightSlateGrey<br>
* hexString: #8787af<br>
* rgb: (r=135, b=175, g=135)<br>
* hsl: (s=20, h=240, l=60)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#8787af"></div><br/><br/>*/
short LIGHTSLATEGREY = 103;



/* ######################################################
######################## navy ########################
###################################################### */

/**
* colorId: 17<br>
* name: NavyBlue<br>
* hexString: #00005f<br>
* rgb: (r=0, b=95, g=0)<br>
* hsl: (s=100, h=240, l=18)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#00005f"></div><br/><br/>*/
short NAVYBLUE = 17;



/* ########################################################
######################## purple ########################
######################################################## */

/**
* colorId: 104<br>
* name: MediumPurple<br>
* hexString: #8787d7<br>
* rgb: (r=135, b=215, g=135)<br>
* hsl: (s=50, h=240, l=68)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#8787d7"></div><br/><br/>*/
short MEDIUMPURPLE = 104;

/**
* colorId: 141<br>
* name: MediumPurple1<br>
* hexString: #af87ff<br>
* rgb: (r=175, b=255, g=135)<br>
* hsl: (s=100, h=260, l=76)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#af87ff"></div><br/><br/>*/
short MEDIUMPURPLE_1 = 141;

/**
* colorId: 135<br>
* name: MediumPurple2<br>
* hexString: #af5fff<br>
* rgb: (r=175, b=255, g=95)<br>
* hsl: (s=100, h=270, l=68)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#af5fff"></div><br/><br/>*/
short MEDIUMPURPLE_2 = 135;

/**
* colorId: 140<br>
* name: MediumPurple2<br>
* hexString: #af87d7<br>
* rgb: (r=175, b=215, g=135)<br>
* hsl: (s=50, h=270, l=68)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#af87d7"></div><br/><br/>*/
short MEDIUMPURPLE_2_2 = 140;

/**
* colorId: 97<br>
* name: MediumPurple3<br>
* hexString: #875faf<br>
* rgb: (r=135, b=175, g=95)<br>
* hsl: (s=33, h=270, l=52)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#875faf"></div><br/><br/>*/
short MEDIUMPURPLE_3 = 97;

/**
* colorId: 98<br>
* name: MediumPurple3<br>
* hexString: #875fd7<br>
* rgb: (r=135, b=215, g=95)<br>
* hsl: (s=60, h=260, l=60)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#875fd7"></div><br/><br/>*/
short MEDIUMPURPLE_3_2 = 98;

/**
* colorId: 60<br>
* name: MediumPurple4<br>
* hexString: #5f5f87<br>
* rgb: (r=95, b=135, g=95)<br>
* hsl: (s=17, h=240, l=45)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#5f5f87"></div><br/><br/>*/
short MEDIUMPURPLE_4 = 60;

/**
* colorId: 93<br>
* name: Purple<br>
* hexString: #8700ff<br>
* rgb: (r=135, b=255, g=0)<br>
* hsl: (s=100, h=271.764705882353, l=50)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#8700ff"></div><br/><br/>*/
short PURPLE_1_1 = 93;

/**
* colorId: 129<br>
* name: Purple<br>
* hexString: #af00ff<br>
* rgb: (r=175, b=255, g=0)<br>
* hsl: (s=100, h=281.176470588235, l=50)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#af00ff"></div><br/><br/>*/
short PURPLE_1_2 = 129;

/**
* colorId: 56<br>
* name: Purple3<br>
* hexString: #5f00d7<br>
* rgb: (r=95, b=215, g=0)<br>
* hsl: (s=100, h=266.511627906977, l=42)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#5f00d7"></div><br/><br/>*/
short PURPLE_3 = 56;

/**
* colorId: 54<br>
* name: Purple4<br>
* hexString: #5f0087<br>
* rgb: (r=95, b=135, g=0)<br>
* hsl: (s=100, h=282.222222222222, l=26)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#5f0087"></div><br/><br/>*/
short PURPLE_4 = 54;

/**
* colorId: 55<br>
* name: Purple4<br>
* hexString: #5f00af<br>
* rgb: (r=95, b=175, g=0)<br>
* hsl: (s=100, h=272.571428571429, l=34)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#5f00af"></div><br/><br/>*/
short PURPLE_4_2 = 55;



/* #####################################################
######################## red ########################
##################################################### */

/**
* colorId: 52<br>
* name: DarkRed<br>
* hexString: #5f0000<br>
* rgb: (r=95, b=0, g=0)<br>
* hsl: (s=100, h=0, l=18)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#5f0000"></div><br/><br/>*/
short DARKRED = 52;

/**
* colorId: 88<br>
* name: DarkRed<br>
* hexString: #870000<br>
* rgb: (r=135, b=0, g=0)<br>
* hsl: (s=100, h=0, l=26)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#870000"></div><br/><br/>*/
short DARKRED_2 = 88;

/**
* colorId: 131<br>
* name: IndianRed<br>
* hexString: #af5f5f<br>
* rgb: (r=175, b=95, g=95)<br>
* hsl: (s=33, h=0, l=52)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#af5f5f"></div><br/><br/>*/
short INDIANRED = 131;

/**
* colorId: 167<br>
* name: IndianRed<br>
* hexString: #d75f5f<br>
* rgb: (r=215, b=95, g=95)<br>
* hsl: (s=60, h=0, l=60)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#d75f5f"></div><br/><br/>*/
short INDIANRED_2 = 167;

/**
* colorId: 203<br>
* name: IndianRed1<br>
* hexString: #ff5f5f<br>
* rgb: (r=255, b=95, g=95)<br>
* hsl: (s=100, h=0, l=68)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#ff5f5f"></div><br/><br/>*/
short INDIANRED_1 = 203;

/**
* colorId: 204<br>
* name: IndianRed1<br>
* hexString: #ff5f87<br>
* rgb: (r=255, b=135, g=95)<br>
* hsl: (s=100, h=345, l=68)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#ff5f87"></div><br/><br/>*/
short INDIANRED_1_2 = 204;

/**
* colorId: 126<br>
* name: MediumVioletRed<br>
* hexString: #af0087<br>
* rgb: (r=175, b=135, g=0)<br>
* hsl: (s=100, h=313.714285714286, l=34)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#af0087"></div><br/><br/>*/
short MEDIUMVIOLETRED = 126;

/**
* colorId: 202<br>
* name: OrangeRed1<br>
* hexString: #ff5f00<br>
* rgb: (r=255, b=0, g=95)<br>
* hsl: (s=100, h=22.3529411764706, l=50)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#ff5f00"></div><br/><br/>*/
short ORANGERED_1 = 202;

/**
* colorId: 211<br>
* name: PaleVioletRed1<br>
* hexString: #ff87af<br>
* rgb: (r=255, b=175, g=135)<br>
* hsl: (s=100, h=340, l=76)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#ff87af"></div><br/><br/>*/
short PALEVIOLETRED_1 = 211;

/**
* colorId: 196<br>
* name: Red1<br>
* hexString: #ff0000<br>
* rgb: (r=255, b=0, g=0)<br>
* hsl: (s=100, h=0, l=50)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#ff0000"></div><br/><br/>*/
short RED_1 = 196;

/**
* colorId: 124<br>
* name: Red3<br>
* hexString: #af0000<br>
* rgb: (r=175, b=0, g=0)<br>
* hsl: (s=100, h=0, l=34)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#af0000"></div><br/><br/>*/
short RED_3 = 124;

/**
* colorId: 160<br>
* name: Red3<br>
* hexString: #d70000<br>
* rgb: (r=215, b=0, g=0)<br>
* hsl: (s=100, h=0, l=42)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#d70000"></div><br/><br/>*/
short RED_3_2 = 160;



/* #######################################################
######################## white ########################
####################################################### */

/**
* colorId: 223<br>
* name: NavajoWhite1<br>
* hexString: #ffd7af<br>
* rgb: (r=255, b=175, g=215)<br>
* hsl: (s=100, h=30, l=84)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#ffd7af"></div><br/><br/>*/
short NAVAJOWHITE_1 = 223;

/**
* colorId: 144<br>
* name: NavajoWhite3<br>
* hexString: #afaf87<br>
* rgb: (r=175, b=135, g=175)<br>
* hsl: (s=20, h=60, l=60)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#afaf87"></div><br/><br/>*/
short NAVAJOWHITE_3 = 144;



/* ########################################################
######################## yellow ########################
######################################################## */

/**
* colorId: 187<br>
* name: LightYellow3<br>
* hexString: #d7d7af<br>
* rgb: (r=215, b=175, g=215)<br>
* hsl: (s=33, h=60, l=76)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#d7d7af"></div><br/><br/>*/
short LIGHTYELLOW_3 = 187;

/**
* colorId: 226<br>
* name: Yellow1<br>
* hexString: #ffff00<br>
* rgb: (r=255, b=0, g=255)<br>
* hsl: (s=100, h=60, l=50)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#ffff00"></div><br/><br/>*/
short YELLOW_1 = 226;

/**
* colorId: 190<br>
* name: Yellow2<br>
* hexString: #d7ff00<br>
* rgb: (r=215, b=0, g=255)<br>
* hsl: (s=100, h=69.4117647058823, l=50)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#d7ff00"></div><br/><br/>*/
short YELLOW_2 = 190;

/**
* colorId: 148<br>
* name: Yellow3<br>
* hexString: #afd700<br>
* rgb: (r=175, b=0, g=215)<br>
* hsl: (s=100, h=71.1627906976744, l=42)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#afd700"></div><br/><br/>*/
short YELLOW_3 = 148;

/**
* colorId: 184<br>
* name: Yellow3<br>
* hexString: #d7d700<br>
* rgb: (r=215, b=0, g=215)<br>
* hsl: (s=100, h=60, l=42)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#d7d700"></div><br/><br/>*/
short YELLOW_3_2 = 184;

/**
* colorId: 100<br>
* name: Yellow4<br>
* hexString: #878700<br>
* rgb: (r=135, b=0, g=135)<br>
* hsl: (s=100, h=60, l=26)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#878700"></div><br/><br/>*/
short YELLOW_4 = 100;

/**
* colorId: 106<br>
* name: Yellow4<br>
* hexString: #87af00<br>
* rgb: (r=135, b=0, g=175)<br>
* hsl: (s=100, h=73.7142857142857, l=34)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#87af00"></div><br/><br/>*/
short YELLOW_4_2 = 106;



/* ############################################################
######################## chartreuse ########################
############################################################ */

/**
* colorId: 118<br>
* name: Chartreuse1<br>
* hexString: #87ff00<br>
* rgb: (r=135, b=0, g=255)<br>
* hsl: (s=100, h=88.2352941176471, l=50)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#87ff00"></div><br/><br/>*/
short CHARTREUSE_1 = 118;

/**
* colorId: 112<br>
* name: Chartreuse2<br>
* hexString: #87d700<br>
* rgb: (r=135, b=0, g=215)<br>
* hsl: (s=100, h=82.3255813953488, l=42)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#87d700"></div><br/><br/>*/
short CHARTREUSE_2 = 112;



/* #######################################################
######################## coral ########################
####################################################### */

/**
* colorId: 210<br>
* name: LightCoral<br>
* hexString: #ff8787<br>
* rgb: (r=255, b=135, g=135)<br>
* hsl: (s=100, h=0, l=76)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#ff8787"></div><br/><br/>*/
short LIGHTCORAL = 210;



/* ##########################################################
######################## cornsilk ########################
########################################################## */

/**
* colorId: 230<br>
* name: Cornsilk1<br>
* hexString: #ffffd7<br>
* rgb: (r=255, b=215, g=255)<br>
* hsl: (s=100, h=60, l=92)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#ffffd7"></div><br/><br/>*/
short CORNSILK_1 = 230;



/* ######################################################
######################## cyan ########################
###################################################### */

/**
* colorId: 195<br>
* name: LightCyan1<br>
* hexString: #d7ffff<br>
* rgb: (r=215, b=255, g=255)<br>
* hsl: (s=100, h=180, l=92)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#d7ffff"></div><br/><br/>*/
short LIGHTCYAN_1 = 195;

/**
* colorId: 152<br>
* name: LightCyan3<br>
* hexString: #afd7d7<br>
* rgb: (r=175, b=215, g=215)<br>
* hsl: (s=33, h=180, l=76)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#afd7d7"></div><br/><br/>*/
short LIGHTCYAN_3 = 152;



/* ##########################################################
######################## deeppink ########################
########################################################## */

/**
* colorId: 198<br>
* name: DeepPink1<br>
* hexString: #ff0087<br>
* rgb: (r=255, b=135, g=0)<br>
* hsl: (s=100, h=328.235294117647, l=50)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#ff0087"></div><br/><br/>*/
short DEEPPINK_1 = 198;

/**
* colorId: 199<br>
* name: DeepPink1<br>
* hexString: #ff00af<br>
* rgb: (r=255, b=175, g=0)<br>
* hsl: (s=100, h=318.823529411765, l=50)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#ff00af"></div><br/><br/>*/
short DEEPPINK_1_2 = 199;

/**
* colorId: 197<br>
* name: DeepPink2<br>
* hexString: #ff005f<br>
* rgb: (r=255, b=95, g=0)<br>
* hsl: (s=100, h=337.647058823529, l=50)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#ff005f"></div><br/><br/>*/
short DEEPPINK_2 = 197;

/**
* colorId: 161<br>
* name: DeepPink3<br>
* hexString: #d7005f<br>
* rgb: (r=215, b=95, g=0)<br>
* hsl: (s=100, h=333.488372093023, l=42)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#d7005f"></div><br/><br/>*/
short DEEPPINK_3 = 161;

/**
* colorId: 162<br>
* name: DeepPink3<br>
* hexString: #d70087<br>
* rgb: (r=215, b=135, g=0)<br>
* hsl: (s=100, h=322.325581395349, l=42)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#d70087"></div><br/><br/>*/
short DEEPPINK_3_2 = 162;

/**
* colorId: 89<br>
* name: DeepPink4<br>
* hexString: #87005f<br>
* rgb: (r=135, b=95, g=0)<br>
* hsl: (s=100, h=317.777777777778, l=26)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#87005f"></div><br/><br/>*/
short DEEPPINK_4 = 89;

/**
* colorId: 125<br>
* name: DeepPink4<br>
* hexString: #af005f<br>
* rgb: (r=175, b=95, g=0)<br>
* hsl: (s=100, h=327.428571428571, l=34)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#af005f"></div><br/><br/>*/
short DEEPPINK_4_2 = 125;



/* ######################################################
######################## gold ########################
###################################################### */

/**
* colorId: 220<br>
* name: Gold1<br>
* hexString: #ffd700<br>
* rgb: (r=255, b=0, g=215)<br>
* hsl: (s=100, h=50.5882352941176, l=50)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#ffd700"></div><br/><br/>*/
short GOLD_1 = 220;

/**
* colorId: 142<br>
* name: Gold3<br>
* hexString: #afaf00<br>
* rgb: (r=175, b=0, g=175)<br>
* hsl: (s=100, h=60, l=34)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#afaf00"></div><br/><br/>*/
short GOLD_3 = 142;

/**
* colorId: 178<br>
* name: Gold3<br>
* hexString: #d7af00<br>
* rgb: (r=215, b=0, g=175)<br>
* hsl: (s=100, h=48.8372093023256, l=42)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#d7af00"></div><br/><br/>*/
short GOLD_3_2 = 178;



/* ###########################################################
######################## goldenrod ########################
########################################################### */

/**
* colorId: 136<br>
* name: DarkGoldenrod<br>
* hexString: #af8700<br>
* rgb: (r=175, b=0, g=135)<br>
* hsl: (s=100, h=46.2857142857143, l=34)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#af8700"></div><br/><br/>*/
short DARKGOLDENROD = 136;

/**
* colorId: 227<br>
* name: LightGoldenrod1<br>
* hexString: #ffff5f<br>
* rgb: (r=255, b=95, g=255)<br>
* hsl: (s=100, h=60, l=68)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#ffff5f"></div><br/><br/>*/
short LIGHTGOLDENROD_1 = 227;

/**
* colorId: 186<br>
* name: LightGoldenrod2<br>
* hexString: #d7d787<br>
* rgb: (r=215, b=135, g=215)<br>
* hsl: (s=50, h=60, l=68)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#d7d787"></div><br/><br/>*/
short LIGHTGOLDENROD_2 = 186;

/**
* colorId: 221<br>
* name: LightGoldenrod2<br>
* hexString: #ffd75f<br>
* rgb: (r=255, b=95, g=215)<br>
* hsl: (s=100, h=45, l=68)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#ffd75f"></div><br/><br/>*/
short LIGHTGOLDENROD_2_2 = 221;

/**
* colorId: 222<br>
* name: LightGoldenrod2<br>
* hexString: #ffd787<br>
* rgb: (r=255, b=135, g=215)<br>
* hsl: (s=100, h=40, l=76)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#ffd787"></div><br/><br/>*/
short LIGHTGOLDENROD_2_3 = 222;

/**
* colorId: 179<br>
* name: LightGoldenrod3<br>
* hexString: #d7af5f<br>
* rgb: (r=215, b=95, g=175)<br>
* hsl: (s=60, h=40, l=60)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#d7af5f"></div><br/><br/>*/
short LIGHTGOLDENROD_3 = 179;



/* ##########################################################
######################## honeydew ########################
########################################################## */

/**
* colorId: 194<br>
* name: Honeydew2<br>
* hexString: #d7ffd7<br>
* rgb: (r=215, b=215, g=255)<br>
* hsl: (s=100, h=120, l=92)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#d7ffd7"></div><br/><br/>*/
short HONEYDEW_2 = 194;



/* #########################################################
######################## hotpink ########################
######################################################### */

/**
* colorId: 205<br>
* name: HotPink<br>
* hexString: #ff5faf<br>
* rgb: (r=255, b=175, g=95)<br>
* hsl: (s=100, h=330, l=68)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#ff5faf"></div><br/><br/>*/
short HOTPINK = 205;

/**
* colorId: 206<br>
* name: HotPink<br>
* hexString: #ff5fd7<br>
* rgb: (r=255, b=215, g=95)<br>
* hsl: (s=100, h=315, l=68)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#ff5fd7"></div><br/><br/>*/
short HOTPINK_1_2 = 206;

/**
* colorId: 169<br>
* name: HotPink2<br>
* hexString: #d75faf<br>
* rgb: (r=215, b=175, g=95)<br>
* hsl: (s=60, h=320, l=60)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#d75faf"></div><br/><br/>*/
short HOTPINK_2 = 169;

/**
* colorId: 132<br>
* name: HotPink3<br>
* hexString: #af5f87<br>
* rgb: (r=175, b=135, g=95)<br>
* hsl: (s=33, h=330, l=52)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#af5f87"></div><br/><br/>*/
short HOTPINK_3 = 132;

/**
* colorId: 168<br>
* name: HotPink3<br>
* hexString: #d75f87<br>
* rgb: (r=215, b=135, g=95)<br>
* hsl: (s=60, h=340, l=60)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#d75f87"></div><br/><br/>*/
short HOTPINK_3_2 = 168;



/* #######################################################
######################## khaki ########################
####################################################### */

/**
* colorId: 143<br>
* name: DarkKhaki<br>
* hexString: #afaf5f<br>
* rgb: (r=175, b=95, g=175)<br>
* hsl: (s=33, h=60, l=52)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#afaf5f"></div><br/><br/>*/
short DARKKHAKI = 143;

/**
* colorId: 228<br>
* name: Khaki1<br>
* hexString: #ffff87<br>
* rgb: (r=255, b=135, g=255)<br>
* hsl: (s=100, h=60, l=76)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#ffff87"></div><br/><br/>*/
short KHAKI_1 = 228;

/**
* colorId: 185<br>
* name: Khaki3<br>
* hexString: #d7d75f<br>
* rgb: (r=215, b=95, g=215)<br>
* hsl: (s=60, h=60, l=60)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#d7d75f"></div><br/><br/>*/
short KHAKI_3 = 185;



/* #########################################################
######################## magenta ########################
######################################################### */

/**
* colorId: 90<br>
* name: DarkMagenta<br>
* hexString: #870087<br>
* rgb: (r=135, b=135, g=0)<br>
* hsl: (s=100, h=300, l=26)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#870087"></div><br/><br/>*/
short DARKMAGENTA = 90;

/**
* colorId: 91<br>
* name: DarkMagenta<br>
* hexString: #8700af<br>
* rgb: (r=135, b=175, g=0)<br>
* hsl: (s=100, h=286.285714285714, l=34)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#8700af"></div><br/><br/>*/
short DARKMAGENTA_2 = 91;

/**
* colorId: 201<br>
* name: Magenta1<br>
* hexString: #ff00ff<br>
* rgb: (r=255, b=255, g=0)<br>
* hsl: (s=100, h=300, l=50)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#ff00ff"></div><br/><br/>*/
short MAGENTA_1 = 201;

/**
* colorId: 165<br>
* name: Magenta2<br>
* hexString: #d700ff<br>
* rgb: (r=215, b=255, g=0)<br>
* hsl: (s=100, h=290.588235294118, l=50)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#d700ff"></div><br/><br/>*/
short MAGENTA_2 = 165;

/**
* colorId: 200<br>
* name: Magenta2<br>
* hexString: #ff00d7<br>
* rgb: (r=255, b=215, g=0)<br>
* hsl: (s=100, h=309.411764705882, l=50)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#ff00d7"></div><br/><br/>*/
short MAGENTA_2_2 = 200;

/**
* colorId: 127<br>
* name: Magenta3<br>
* hexString: #af00af<br>
* rgb: (r=175, b=175, g=0)<br>
* hsl: (s=100, h=300, l=34)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#af00af"></div><br/><br/>*/
short MAGENTA_3 = 127;

/**
* colorId: 163<br>
* name: Magenta3<br>
* hexString: #d700af<br>
* rgb: (r=215, b=175, g=0)<br>
* hsl: (s=100, h=311.162790697674, l=42)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#d700af"></div><br/><br/>*/
short MAGENTA_3_2 = 163;

/**
* colorId: 164<br>
* name: Magenta3<br>
* hexString: #d700d7<br>
* rgb: (r=215, b=215, g=0)<br>
* hsl: (s=100, h=300, l=42)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#d700d7"></div><br/><br/>*/
short MAGENTA_3_3 = 164;



/* ###########################################################
######################## mistyrose ########################
########################################################### */

/**
* colorId: 224<br>
* name: MistyRose1<br>
* hexString: #ffd7d7<br>
* rgb: (r=255, b=215, g=215)<br>
* hsl: (s=100, h=0, l=92)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#ffd7d7"></div><br/><br/>*/
short MISTYROSE_1 = 224;

/**
* colorId: 181<br>
* name: MistyRose3<br>
* hexString: #d7afaf<br>
* rgb: (r=215, b=175, g=175)<br>
* hsl: (s=33, h=0, l=76)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#d7afaf"></div><br/><br/>*/
short MISTYROSE_3 = 181;



/* ########################################################
######################## orange ########################
######################################################## */

/**
* colorId: 208<br>
* name: DarkOrange<br>
* hexString: #ff8700<br>
* rgb: (r=255, b=0, g=135)<br>
* hsl: (s=100, h=31.7647058823529, l=50)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#ff8700"></div><br/><br/>*/
short DARKORANGE = 208;

/**
* colorId: 130<br>
* name: DarkOrange3<br>
* hexString: #af5f00<br>
* rgb: (r=175, b=0, g=95)<br>
* hsl: (s=100, h=32.5714285714286, l=34)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#af5f00"></div><br/><br/>*/
short DARKORANGE_3 = 130;

/**
* colorId: 166<br>
* name: DarkOrange3<br>
* hexString: #d75f00<br>
* rgb: (r=215, b=0, g=95)<br>
* hsl: (s=100, h=26.5116279069767, l=42)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#d75f00"></div><br/><br/>*/
short DARKORANGE_3_2 = 166;

/**
* colorId: 214<br>
* name: Orange1<br>
* hexString: #ffaf00<br>
* rgb: (r=255, b=0, g=175)<br>
* hsl: (s=100, h=41.1764705882353, l=50)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#ffaf00"></div><br/><br/>*/
short ORANGE_1 = 214;

/**
* colorId: 172<br>
* name: Orange3<br>
* hexString: #d78700<br>
* rgb: (r=215, b=0, g=135)<br>
* hsl: (s=100, h=37.6744186046512, l=42)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#d78700"></div><br/><br/>*/
short ORANGE_3 = 172;

/**
* colorId: 94<br>
* name: Orange4<br>
* hexString: #875f00<br>
* rgb: (r=135, b=0, g=95)<br>
* hsl: (s=100, h=42.2222222222222, l=26)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#875f00"></div><br/><br/>*/
short ORANGE_4 = 94;



/* ########################################################
######################## orchid ########################
######################################################## */

/**
* colorId: 134<br>
* name: MediumOrchid<br>
* hexString: #af5fd7<br>
* rgb: (r=175, b=215, g=95)<br>
* hsl: (s=60, h=280, l=60)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#af5fd7"></div><br/><br/>*/
short MEDIUMORCHID = 134;

/**
* colorId: 171<br>
* name: MediumOrchid1<br>
* hexString: #d75fff<br>
* rgb: (r=215, b=255, g=95)<br>
* hsl: (s=100, h=285, l=68)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#d75fff"></div><br/><br/>*/
short MEDIUMORCHID_1 = 171;

/**
* colorId: 207<br>
* name: MediumOrchid1<br>
* hexString: #ff5fff<br>
* rgb: (r=255, b=255, g=95)<br>
* hsl: (s=100, h=300, l=68)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#ff5fff"></div><br/><br/>*/
short MEDIUMORCHID_1_2 = 207;

/**
* colorId: 133<br>
* name: MediumOrchid3<br>
* hexString: #af5faf<br>
* rgb: (r=175, b=175, g=95)<br>
* hsl: (s=33, h=300, l=52)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#af5faf"></div><br/><br/>*/
short MEDIUMORCHID_3 = 133;

/**
* colorId: 170<br>
* name: Orchid<br>
* hexString: #d75fd7<br>
* rgb: (r=215, b=215, g=95)<br>
* hsl: (s=60, h=300, l=60)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#d75fd7"></div><br/><br/>*/
short ORCHID = 170;

/**
* colorId: 213<br>
* name: Orchid1<br>
* hexString: #ff87ff<br>
* rgb: (r=255, b=255, g=135)<br>
* hsl: (s=100, h=300, l=76)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#ff87ff"></div><br/><br/>*/
short ORCHID_1 = 213;

/**
* colorId: 212<br>
* name: Orchid2<br>
* hexString: #ff87d7<br>
* rgb: (r=255, b=215, g=135)<br>
* hsl: (s=100, h=320, l=76)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#ff87d7"></div><br/><br/>*/
short ORCHID_2 = 212;



/* ######################################################
######################## pink ########################
###################################################### */

/**
* colorId: 217<br>
* name: LightPink1<br>
* hexString: #ffafaf<br>
* rgb: (r=255, b=175, g=175)<br>
* hsl: (s=100, h=0, l=84)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#ffafaf"></div><br/><br/>*/
short LIGHTPINK_1 = 217;

/**
* colorId: 174<br>
* name: LightPink3<br>
* hexString: #d78787<br>
* rgb: (r=215, b=135, g=135)<br>
* hsl: (s=50, h=0, l=68)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#d78787"></div><br/><br/>*/
short LIGHTPINK_3 = 174;

/**
* colorId: 95<br>
* name: LightPink4<br>
* hexString: #875f5f<br>
* rgb: (r=135, b=95, g=95)<br>
* hsl: (s=17, h=0, l=45)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#875f5f"></div><br/><br/>*/
short LIGHTPINK_4 = 95;

/**
* colorId: 218<br>
* name: Pink1<br>
* hexString: #ffafd7<br>
* rgb: (r=255, b=215, g=175)<br>
* hsl: (s=100, h=330, l=84)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#ffafd7"></div><br/><br/>*/
short PINK_1 = 218;

/**
* colorId: 175<br>
* name: Pink3<br>
* hexString: #d787af<br>
* rgb: (r=215, b=175, g=135)<br>
* hsl: (s=50, h=330, l=68)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#d787af"></div><br/><br/>*/
short PINK_3 = 175;



/* ######################################################
######################## plum ########################
###################################################### */

/**
* colorId: 219<br>
* name: Plum1<br>
* hexString: #ffafff<br>
* rgb: (r=255, b=255, g=175)<br>
* hsl: (s=100, h=300, l=84)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#ffafff"></div><br/><br/>*/
short PLUM_1 = 219;

/**
* colorId: 183<br>
* name: Plum2<br>
* hexString: #d7afff<br>
* rgb: (r=215, b=255, g=175)<br>
* hsl: (s=100, h=270, l=84)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#d7afff"></div><br/><br/>*/
short PLUM_2 = 183;

/**
* colorId: 176<br>
* name: Plum3<br>
* hexString: #d787d7<br>
* rgb: (r=215, b=215, g=135)<br>
* hsl: (s=50, h=300, l=68)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#d787d7"></div><br/><br/>*/
short PLUM_3 = 176;

/**
* colorId: 96<br>
* name: Plum4<br>
* hexString: #875f87<br>
* rgb: (r=135, b=135, g=95)<br>
* hsl: (s=17, h=300, l=45)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#875f87"></div><br/><br/>*/
short PLUM_4 = 96;



/* ###########################################################
######################## rosybrown ########################
########################################################### */

/**
* colorId: 138<br>
* name: RosyBrown<br>
* hexString: #af8787<br>
* rgb: (r=175, b=135, g=135)<br>
* hsl: (s=20, h=0, l=60)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#af8787"></div><br/><br/>*/
short ROSYBROWN = 138;



/* ########################################################
######################## salmon ########################
######################################################## */

/**
* colorId: 216<br>
* name: LightSalmon1<br>
* hexString: #ffaf87<br>
* rgb: (r=255, b=135, g=175)<br>
* hsl: (s=100, h=20, l=76)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#ffaf87"></div><br/><br/>*/
short LIGHTSALMON_1 = 216;

/**
* colorId: 137<br>
* name: LightSalmon3<br>
* hexString: #af875f<br>
* rgb: (r=175, b=95, g=135)<br>
* hsl: (s=33, h=30, l=52)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#af875f"></div><br/><br/>*/
short LIGHTSALMON_3 = 137;

/**
* colorId: 173<br>
* name: LightSalmon3<br>
* hexString: #d7875f<br>
* rgb: (r=215, b=95, g=135)<br>
* hsl: (s=60, h=20, l=60)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#d7875f"></div><br/><br/>*/
short LIGHTSALMON_3_2 = 173;

/**
* colorId: 209<br>
* name: Salmon1<br>
* hexString: #ff875f<br>
* rgb: (r=255, b=95, g=135)<br>
* hsl: (s=100, h=15, l=68)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#ff875f"></div><br/><br/>*/
short SALMON_1 = 209;



/* ############################################################
######################## sandybrown ########################
############################################################ */

/**
* colorId: 215<br>
* name: SandyBrown<br>
* hexString: #ffaf5f<br>
* rgb: (r=255, b=95, g=175)<br>
* hsl: (s=100, h=30, l=68)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#ffaf5f"></div><br/><br/>*/
short SANDYBROWN = 215;



/* ###########################################################
######################## slategray ########################
########################################################### */

/**
* colorId: 123<br>
* name: DarkSlateGray1<br>
* hexString: #87ffff<br>
* rgb: (r=135, b=255, g=255)<br>
* hsl: (s=100, h=180, l=76)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#87ffff"></div><br/><br/>*/
short DARKSLATEGRAY_1 = 123;

/**
* colorId: 116<br>
* name: DarkSlateGray3<br>
* hexString: #87d7d7<br>
* rgb: (r=135, b=215, g=215)<br>
* hsl: (s=50, h=180, l=68)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#87d7d7"></div><br/><br/>*/
short DARKSLATEGRAY_3 = 116;



/* #####################################################
######################## tan ########################
##################################################### */

/**
* colorId: 180<br>
* name: Tan<br>
* hexString: #d7af87<br>
* rgb: (r=215, b=135, g=175)<br>
* hsl: (s=50, h=30, l=68)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#d7af87"></div><br/><br/>*/
short TAN = 180;



/* #########################################################
######################## thistle ########################
######################################################### */

/**
* colorId: 225<br>
* name: Thistle1<br>
* hexString: #ffd7ff<br>
* rgb: (r=255, b=255, g=215)<br>
* hsl: (s=100, h=300, l=92)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#ffd7ff"></div><br/><br/>*/
short THISTLE_1 = 225;

/**
* colorId: 182<br>
* name: Thistle3<br>
* hexString: #d7afd7<br>
* rgb: (r=215, b=215, g=175)<br>
* hsl: (s=33, h=300, l=76)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#d7afd7"></div><br/><br/>*/
short THISTLE_3 = 182;



/* ###########################################################
######################## turquoise ########################
########################################################### */

/**
* colorId: 159<br>
* name: PaleTurquoise1<br>
* hexString: #afffff<br>
* rgb: (r=175, b=255, g=255)<br>
* hsl: (s=100, h=180, l=84)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#afffff"></div><br/><br/>*/
short PALETURQUOISE_1 = 159;



/* ########################################################
######################## violet ########################
######################################################## */

/**
* colorId: 92<br>
* name: DarkViolet<br>
* hexString: #8700d7<br>
* rgb: (r=135, b=215, g=0)<br>
* hsl: (s=100, h=277.674418604651, l=42)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#8700d7"></div><br/><br/>*/
short DARKVIOLET = 92;

/**
* colorId: 128<br>
* name: DarkViolet<br>
* hexString: #af00d7<br>
* rgb: (r=175, b=215, g=0)<br>
* hsl: (s=100, h=288.837209302326, l=42)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#af00d7"></div><br/><br/>*/
short DARKVIOLET_2 = 128;

/**
* colorId: 177<br>
* name: Violet<br>
* hexString: #d787ff<br>
* rgb: (r=215, b=255, g=135)<br>
* hsl: (s=100, h=280, l=76)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#d787ff"></div><br/><br/>*/
short VIOLET = 177;



/* #######################################################
######################## wheat ########################
####################################################### */

/**
* colorId: 229<br>
* name: Wheat1<br>
* hexString: #ffffaf<br>
* rgb: (r=255, b=175, g=255)<br>
* hsl: (s=100, h=60, l=84)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#ffffaf"></div><br/><br/>*/
short WHEAT_1 = 229;

/**
* colorId: 101<br>
* name: Wheat4<br>
* hexString: #87875f<br>
* rgb: (r=135, b=95, g=135)<br>
* hsl: (s=17, h=60, l=45)<br>
* <div style="border:1px solid black;width:80px;height:30px;background-color:#87875f"></div><br/><br/>*/
short WHEAT_4 = 101;


    
}
