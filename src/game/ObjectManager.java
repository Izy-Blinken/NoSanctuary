/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game;

import java.awt.Graphics2D;
import models.FireCamp;
import models.GameObject;
import models.ObjAppleTree;
import models.ObjHouse;
import models.ObjPineTree;
import models.ObjTree;
import models.ObjVehicle;
import models.ObjWoods;

/**
 *
 * @author ADMIN
 */
public class ObjectManager {
    panel gp;
    public GameObject[] objects;
    public GameObject[] objAppleTree;
    public GameObject[] ObjVehicle;
    public GameObject[] ObjHouse;
    public GameObject[] ObjWoods;
    public GameObject[] FireCamp;
    public GameObject[] ObjPineTree;
    
    public ObjectManager(panel gp) {
        this.gp = gp;
        objects = new GameObject[5900];
        objAppleTree = new GameObject[100];
        ObjVehicle = new GameObject[100];
        ObjHouse = new GameObject[10];
        ObjWoods = new GameObject[100];
        FireCamp = new GameObject[100];
        ObjPineTree = new GameObject[100];
        
        setObjects();
    }

    public void setObjects() {

        // COLLISION TREE
        //Top
        int index = 0;

        int[][] posX = {
            // Row 1
            {2,91,190,285,389,476,577,667,766,867,955,1054,1155,1246,1348,1435,1537,1631,1726,1820,1921,2016,2116,2203,2307,2401,2499,2597,2688,2782},
            {-5,94,188,284,383,485,573,672,773,869,956,1061,1155,1245,1345,1445,1534,1632,1726,1824,1919,2014,2112,2213,2305,2402,2495,2590,2691,2788},
            {-5,96,189,290,379,477,573,673,764,865,962,1055,1147,1244,1347,1445,1532,1633,1730,1823,1917,2012,2111,2211,2302,2400,2499,2587,2688,2779},
            {0,94,190,284,386,483,573,674,765,867,961,1059,1151,1253,1344,1443,1532,1630,1728,1828,1918,2014,2108,2203,2300,2400,2499,2591,2690,2787},
            {2,98,194,286,380,481,577,674,773,869,955,1056,1150,1246,1346,1441,1535,1630,1730,1820,1925,2011,2110,2209,2306,2401,2493,2587,2687,2783}
        };

        int[][] posY = {
            // Row 1
            {-10,-10,-10,-10,-10,-10,-10,-10,-10,-10,-10,-10,-10,-10,-10,-10,-10,-10,-10,-10,-10,-10,-10,-10,-10,-10,-10,-10,-10,-10},
            {5,0,0,1,2,0,0,0,0,5,4,0,0,2,0,3,5,0,0,1,0,4,0,2,5,0,0,3,0,1},
            {49,46,51,44,44,53,53,52,49,52,51,51,53,53,47,48,47,45,43,51,51,53,53,52,45,45,51,52,50,44},
            {95,91,100,92,92,93,101,99,95,100,94,94,97,101,98,98,94,92,91,99,100,91,101,94,91,92,94,101,94,93},
            {155,160,145,147,145,158,140,150,155,149,158,139,145,139,141,145,162,140,148,139,150,161,140,145,149,139,158,123,144,150}
        };

        for(int row = 0; row < posX.length; row++){
            for(int col = 0; col < posX[row].length; col++){

                objects[index] = new ObjTree(gp, 1);
                objects[index].worldX = posX[row][col];
                objects[index].worldY = posY[row][col];

                index++;
            }
        }
        
        //Left
        int indexLeft = 300;

        int startX = -100;
        int startY = 119;
        int rows = 30;          // pababa
        int cols = 10;           // 7 columns wide
        int spacingX = 45;      // distance between columns
        int spacingY = 60;      // distance between rows

        java.util.Random rand = new java.util.Random();

        for(int row = 0; row < rows; row++) {
            for(int col = 0; col < cols; col++) {

                int randomOffsetX = rand.nextInt(40) - 15;
                int randomOffsetY = rand.nextInt(30) - 18;

                objects[indexLeft] = new ObjTree(gp, 1);

                objects[indexLeft].worldX =
                        startX + (col * spacingX) + randomOffsetX;

                objects[indexLeft].worldY =
                        startY + (row * spacingY) + randomOffsetY;

                indexLeft++;
            }
        }
        
        // Right
        int indexRight = indexLeft;

        int rightStartX = 2800;
        int rightStartY = 119;

        for(int row = 0; row < rows; row++) {
            for(int col = 0; col < cols; col++) {

                int randomOffsetX = rand.nextInt(40) - 15;
                int randomOffsetY = rand.nextInt(30) - 18;

                objects[indexRight] = new ObjTree(gp, 1);

                objects[indexRight].worldX =
                        rightStartX - (col * spacingX) + randomOffsetX;

                objects[indexRight].worldY =
                        rightStartY + (row * spacingY) + randomOffsetY;

                indexRight++;
            }
        }

        //Bottom
        int index2 = 150;
        int[][] coords = {
            {0,1935},{87,1909},{200,1914},{299,1907},{392,1910},
            {474,1907},{572,1934},{680,1934},{783,1932},{870,1920},
            {962,1917},{1053,1921},{1149,1915},{1256,1935},{1342,1922},
            {1449,1925},{1522,1925},{1625,1906},{1743,1912},{1825,1927},
            {1905,1923},{2015,1931},{2104,1906},{2205,1912},{2291,1921},
            {2405,1928},{2494,1911},{2584,1929},{2679,1918},{2774,1914},

            {0,1979},{81,1956},{203,1982},{286,1962},{379,1964},
            {484,1959},{568,1967},{662,1979},{758,1960},{858,1970},
            {950,1967},{1047,1962},{1157,1957},{1259,1975},{1351,1966},
            {1451,1968},{1539,1972},{1640,1956},{1725,1979},{1830,1981},
            {1907,1961},{2002,1979},{2119,1975},{2201,1962},{2299,1961},
            {2389,1955},{2500,1966},{2602,1967},{2688,1968},{2771,1968},

            {0,2018},{92,2004},{185,2008},{285,2018},{382,2016},
            {483,2006},{581,2009},{658,2008},{778,2021},{872,2012},
            {971,2014},{1051,2023},{1148,2018},{1261,2021},{1329,2002},
            {1436,2023},{1542,2007},{1632,2011},{1728,2017},{1818,2031},
            {1921,2013},{2009,2021},{2112,2007},{2214,2031},{2316,2008},
            {2390,2018},{2487,2022},{2599,2012},{2687,2019},{2789,2016},

            {0,2071},{82,2066},{180,2068},{289,2057},{374,2064},
            {471,2069},{582,2062},{687,2052},{766,2055},{858,2076},
            {972,2055},{1066,2065},{1137,2071},{1253,2056},{1344,2074},
            {1443,2064},{1540,2079},{1641,2055},{1738,2062},{1809,2060},
            {1919,2057},{2003,2062},{2108,2062},{2210,2061},{2316,2075},
            {2395,2051},{2482,2075},{2601,2053},{2686,2053},{2769,2051},

            {9,2112},{104,2098},{197,2112},{299,2112},{377,2112},
            {491,2112},{572,2098},{684,2112},{773,2112},{851,2110},
            {962,2112},{1048,2112},{1137,2112},{1247,2099},{1330,2108},
            {1438,2112},{1547,2097},{1643,2110},{1729,2112},{1834,2112},
            {1912,2105},{2005,2101},{2124,2112},{2217,2112},{2311,2106},
            {2402,2112},{2502,2112},{2597,2105},{2690,2112},{2771,2109}
        };

        for(int i = 0; i < coords.length; i++) {
            objects[index2] = new ObjTree(gp, 1);
            objects[index2].worldX = coords[i][0];
            objects[index2].worldY = coords[i][1];
            index2++;
        }
         

        //Apple Tree
        objAppleTree[0] = new ObjAppleTree(gp,1.5); objAppleTree[0].worldX = 700; objAppleTree[0].worldY = 300;
        objAppleTree[1] = new ObjAppleTree(gp, 1.5); objAppleTree[1].worldX = 1700; objAppleTree[1].worldY = 700;
        objAppleTree[2] = new ObjAppleTree(gp, 1.5); objAppleTree[2].worldX = 2000; objAppleTree[2].worldY = 1200;
        objAppleTree[3] = new ObjAppleTree(gp, 1.5); objAppleTree[3].worldX = 805; objAppleTree[3].worldY = 605;
        objAppleTree[4] = new ObjAppleTree(gp, 1.5); objAppleTree[4].worldX = 598; objAppleTree[4].worldY = 350;
        objAppleTree[5] = new ObjAppleTree(gp, 1.5); objAppleTree[5].worldX = 1500; objAppleTree[5].worldY = 500;
        objAppleTree[6] = new ObjAppleTree(gp, 1.5); objAppleTree[6].worldX = 1205; objAppleTree[6].worldY = 605;
        objAppleTree[7] = new ObjAppleTree(gp, 1.5); objAppleTree[7].worldX = 798; objAppleTree[7].worldY = 350;
        objAppleTree[8] = new ObjAppleTree(gp, 1.5); objAppleTree[8].worldX = 1800; objAppleTree[8].worldY = 400;
        
        //Vehicle
        ObjVehicle[0] = new ObjVehicle(gp, 1); ObjVehicle[0].worldX = 490; ObjVehicle[0].worldY = 250;
        ObjVehicle[1] = new ObjVehicle(gp, 2); ObjVehicle[1].worldX = 1000; ObjVehicle[1].worldY = 1000;
        ObjVehicle[2] = new ObjVehicle(gp, 1); ObjVehicle[2].worldX = 1800; ObjVehicle[2].worldY = 750;
        ObjVehicle[3] = new ObjVehicle(gp, 2); ObjVehicle[3].worldX = 905; ObjVehicle[3].worldY = 900;
        
        //Logs
        ObjWoods[0] = new ObjWoods(gp, 1.5); ObjWoods[0].worldX = 2000; ObjWoods[0].worldY = 250;
        ObjWoods[1] = new ObjWoods(gp, 1.5); ObjWoods[1].worldX = 1500; ObjWoods[1].worldY = 1300;
        ObjWoods[2] = new ObjWoods(gp, 1.5); ObjWoods[2].worldX = 600; ObjWoods[2].worldY = 1100;
        ObjWoods[3] = new ObjWoods(gp, 1.5); ObjWoods[3].worldX = 1000; ObjWoods[3].worldY = 900;
        
        //FireCamp
        
        FireCamp[0] = new FireCamp(gp, 1.5); FireCamp[0].worldX = 800; FireCamp[0].worldY = 500;
        FireCamp[1] = new FireCamp(gp, 1.5); FireCamp[1].worldX = 1900; FireCamp[1].worldY = 400;
        
        // Pine Tree
        ObjPineTree[0] = new ObjPineTree(gp, 2); ObjPineTree[0].worldX = 1600; ObjPineTree[0].worldY = 220;
        ObjPineTree[1] = new ObjPineTree(gp, 2); ObjPineTree[1].worldX = 2000; ObjPineTree[1].worldY = 450;
        ObjPineTree[2] = new ObjPineTree(gp, 2); ObjPineTree[2].worldX = 600; ObjPineTree[2].worldY = 1100;
        
        
        //House
        ObjHouse[0] = new ObjHouse(gp, 2); ObjHouse[0].worldX = 1300; ObjHouse[0].worldY = 250;
        
    }

    public void draw(Graphics2D g2) {
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] == null) continue;
            int screenX = objects[i].worldX - gp.player.worldX + gp.player.screenX;
            int screenY = objects[i].worldY - gp.player.worldY + gp.player.screenY;
            g2.drawImage(objects[i].image, screenX, screenY, null);
        }
        
        for (int i = 0; i < objAppleTree.length; i++) {
            if (objAppleTree[i] == null) continue;
            int screenX = objAppleTree[i].worldX - gp.player.worldX + gp.player.screenX;
            int screenY = objAppleTree[i].worldY - gp.player.worldY + gp.player.screenY;
            g2.drawImage(objAppleTree[i].image, screenX, screenY, null);
        }
        
        for (int i = 0; i < ObjVehicle.length; i++) {
            if (ObjVehicle[i] == null) continue;
            int screenX = ObjVehicle[i].worldX - gp.player.worldX + gp.player.screenX;
            int screenY = ObjVehicle[i].worldY - gp.player.worldY + gp.player.screenY;
            g2.drawImage(ObjVehicle[i].image, screenX, screenY, null);
        }
        
        for (int i = 0; i < ObjHouse.length; i++) {
            if (ObjHouse[i] == null) continue;
            int screenX = ObjHouse[i].worldX - gp.player.worldX + gp.player.screenX;
            int screenY = ObjHouse[i].worldY - gp.player.worldY + gp.player.screenY;
            g2.drawImage(ObjHouse[i].image, screenX, screenY, null);
        }
         
        for (int i = 0; i < ObjWoods.length; i++) {
            if (ObjWoods[i] == null) continue;
            int screenX = ObjWoods[i].worldX - gp.player.worldX + gp.player.screenX;
            int screenY = ObjWoods[i].worldY - gp.player.worldY + gp.player.screenY;
            g2.drawImage(ObjWoods[i].image, screenX, screenY, null);
        }
        
        for (int i = 0; i < FireCamp.length; i++) {
            if (FireCamp[i] == null) continue;
            int screenX = FireCamp[i].worldX - gp.player.worldX + gp.player.screenX;
            int screenY = FireCamp[i].worldY - gp.player.worldY + gp.player.screenY;
            g2.drawImage(FireCamp[i].image, screenX, screenY, null);
        }
        
        for (int i = 0; i < ObjPineTree.length; i++) {
            if (ObjPineTree[i] == null) continue;
            int screenX = ObjPineTree[i].worldX - gp.player.worldX + gp.player.screenX;
            int screenY = ObjPineTree[i].worldY - gp.player.worldY + gp.player.screenY;
            g2.drawImage(ObjPineTree[i].image, screenX, screenY, null);
        }
    }
}
