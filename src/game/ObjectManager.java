package game;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import models.FireCamp;
import models.GameObject;
import models.ObjAppleItem;
import models.ObjAppleTree;
import models.ObjHouse;
import models.ObjInterior;
import models.ObjPineTree;
import models.ObjPortal;
import models.ObjRiddle;
import models.ObjTree;
import models.ObjTreeTop;
import models.ObjTreeTopBR;
import models.ObjVehicle;
import models.ObjWoodItem;
import models.ObjWoods;

public class ObjectManager {

    panel gp;
    public GameObject[] objects;
    public GameObject[] objAppleTree;
    public GameObject[] ObjVehicle;
    public GameObject[] ObjHouse;
    public GameObject[] ObjWoods;
    public GameObject[] FireCamp;
    public GameObject[] ObjPineTree;
    public GameObject[] ObjTreeTop;
    public GameObject[] ObjTreeTopBR;
    public ObjInterior  interior;
    public GameObject[] appleItems;
    public GameObject[] woodItems;

    // --- Riddle tablets ---
    public GameObject[] riddleObjects;

    // --- Portal ---
    public ObjPortal portal;
    public boolean   portalVisible = false;

    public ObjectManager(panel gp) {
        this.gp = gp;
        objects      = new GameObject[5900];
        objAppleTree = new GameObject[100];
        ObjVehicle   = new GameObject[100];
        ObjHouse     = new GameObject[10];
        ObjWoods     = new GameObject[100];
        FireCamp     = new GameObject[100];
        ObjPineTree  = new GameObject[100];
        ObjTreeTop   = new GameObject[100];
        ObjTreeTopBR = new GameObject[100];
        interior     = new models.ObjInterior(gp);
        appleItems   = new GameObject[50];
        woodItems    = new GameObject[50];
        riddleObjects = new GameObject[3];
        portal = new ObjPortal(0, 0); // position set randomly in setObjects()
        setObjects();
        spawnCollectibles();
    }

    public void revealPortal() { portalVisible = true; }

    public void update() {
        if (gp.tileM.currentMap == 2) interior.update();
        if (gp.tileM.currentMap == 1 && portalVisible) portal.update();
    }

    public void setObjects() {

        int index = 0;
        int[][] posX = {
            {2,91,190,285,389,476,577,667,766,867,955,1054,1155,1246,1348,1435,1537,1631,1726,1820,1921,2016,2116,2203,2307,2401,2499,2597,2688,2782},
            {-5,94,188,284,383,485,573,672,773,869,956,1061,1155,1245,1345,1445,1534,1632,1726,1824,1919,2014,2112,2213,2305,2402,2495,2590,2691,2788},
            {-5,96,189,290,379,477,573,673,764,865,962,1055,1147,1244,1347,1445,1532,1633,1730,1823,1917,2012,2111,2211,2302,2400,2499,2587,2688,2779},
            {0,94,190,284,386,483,573,674,765,867,961,1059,1151,1253,1344,1443,1532,1630,1728,1828,1918,2014,2108,2203,2300,2400,2499,2591,2690,2787},
            {2,98,194,286,380,481,577,674,773,869,955,1056,1150,1246,1346,1441,1535,1630,1730,1820,1925,2011,2110,2209,2306,2401,2493,2587,2687,2783}
        };
        int[][] posY = {
            {-10,-10,-10,-10,-10,-10,-10,-10,-10,-10,-10,-10,-10,-10,-10,-10,-10,-10,-10,-10,-10,-10,-10,-10,-10,-10,-10,-10,-10,-10},
            {5,0,0,1,2,0,0,0,0,5,4,0,0,2,0,3,5,0,0,1,0,4,0,2,5,0,0,3,0,1},
            {49,46,51,44,44,53,53,52,49,52,51,51,53,53,47,48,47,45,43,51,51,53,53,52,45,45,51,52,50,44},
            {95,91,100,92,92,93,101,99,95,100,94,94,97,101,98,98,94,92,91,99,100,91,101,94,91,92,94,101,94,93},
            {155,160,145,147,145,158,140,150,155,149,158,139,145,139,141,145,162,140,148,139,150,161,140,145,149,139,158,123,144,150}
        };
        for (int row = 0; row < posX.length; row++) {
            for (int col = 0; col < posX[row].length; col++) {
                objects[index] = new ObjTree(gp, 1);
                objects[index].worldX = posX[row][col];
                objects[index].worldY = posY[row][col];
                index++;
            }
        }

        int indexLeft = 300;
        int startX = -100;
        int startY = 119;
        int rows = 30;
        int cols = 10;
        int spacingX = 45;
        int spacingY = 60;
        java.util.Random rand = new java.util.Random();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int randomOffsetX = rand.nextInt(40) - 15;
                int randomOffsetY = rand.nextInt(30) - 18;
                objects[indexLeft] = new ObjTree(gp, 1);
                objects[indexLeft].worldX = startX + (col * spacingX) + randomOffsetX;
                objects[indexLeft].worldY = startY + (row * spacingY) + randomOffsetY;
                indexLeft++;
            }
        }

        int indexRight = indexLeft;
        int rightStartX = 2800;
        int rightStartY = 119;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int randomOffsetX = rand.nextInt(40) - 15;
                int randomOffsetY = rand.nextInt(30) - 18;
                objects[indexRight] = new ObjTree(gp, 1);
                objects[indexRight].worldX = rightStartX - (col * spacingX) + randomOffsetX;
                objects[indexRight].worldY = rightStartY + (row * spacingY) + randomOffsetY;
                indexRight++;
            }
        }

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
            {2402,2112},{2502,2112},{2597,2105},{2700,2112},{2771,2109}
        };
        for (int i = 0; i < coords.length; i++) {
            objects[index2] = new ObjTree(gp, 1);
            objects[index2].worldX = coords[i][0];
            objects[index2].worldY = coords[i][1];
            index2++;
        }

        int index3 = 0;
        int[][] coordsTop = {
            {-10,1935},{77,1909},{180,1914},{289,1907},{372,1910},
            {-10,1979},{71,1956},{183,1982},{276,1962},{359,1964},
            {-10,2018},{82,2004},{165,2008},{275,2018},{372,2016},
            {-10,2071},{72,2066},{170,2068},{279,2057},{364,2064},
            {-19,2112},{94,2098},{187,2112},{289,2112},{367,2112}
        };
        for (int i = 0; i < coordsTop.length; i++) {
            ObjTreeTop[index3] = new ObjTreeTop(gp, 1.6);
            ObjTreeTop[index3].worldX = coordsTop[i][0] - 30;
            ObjTreeTop[index3].worldY = coordsTop[i][1] - 10;
            index3++;
        }

        int index4 = 0;
        int[][] coordsTopRight = {
            {2415,1928},{2594,1911},{2594,1929},{2689,1918},{2784,1914},
            {2399,1955},{2600,1966},{2612,1967},{2698,1968},{2781,1968},
            {2400,2018},{2497,2022},{2609,2012},{2697,2019},{2799,2016},
            {2405,2051},{2492,2075},{2611,2053},{2696,2053},{2779,2051},
            {2502,2112},{2512,2112},{2607,2105},{2700,2112},{2781,2109}
        };
        for (int i = 0; i < coordsTopRight.length; i++) {
            ObjTreeTopBR[index4] = new ObjTreeTopBR(gp, 1.6);
            ObjTreeTopBR[index4].worldX = coordsTopRight[i][0] - 30;
            ObjTreeTopBR[index4].worldY = coordsTopRight[i][1] - 10;
            index4++;
        }

        objAppleTree[0] = new ObjAppleTree(gp, 1.5); objAppleTree[0].worldX = 700;  objAppleTree[0].worldY = 100;
        objAppleTree[1] = new ObjAppleTree(gp, 1.5); objAppleTree[1].worldX = 700;  objAppleTree[1].worldY = 700;
        objAppleTree[2] = new ObjAppleTree(gp, 1.5); objAppleTree[2].worldX = 2000; objAppleTree[2].worldY = 1200;
        objAppleTree[3] = new ObjAppleTree(gp, 1.5); objAppleTree[3].worldX = 805;  objAppleTree[3].worldY = 605;
        objAppleTree[4] = new ObjAppleTree(gp, 1.5); objAppleTree[4].worldX = 750;  objAppleTree[4].worldY = 110;
        objAppleTree[5] = new ObjAppleTree(gp, 1.5); objAppleTree[5].worldX = 356;  objAppleTree[5].worldY = 500;
        objAppleTree[6] = new ObjAppleTree(gp, 1.5); objAppleTree[6].worldX = 500;  objAppleTree[6].worldY = 605;
        objAppleTree[7] = new ObjAppleTree(gp, 1.5); objAppleTree[7].worldX = 398;  objAppleTree[7].worldY = 750;
        objAppleTree[8] = new ObjAppleTree(gp, 1.5); objAppleTree[8].worldX = 1800; objAppleTree[8].worldY = 400;

        ObjVehicle[0] = new ObjVehicle(gp, 1, 1.5); ObjVehicle[0].worldX = 490;  ObjVehicle[0].worldY = 250;
        ObjVehicle[1] = new ObjVehicle(gp, 2, 1.5); ObjVehicle[1].worldX = 1000; ObjVehicle[1].worldY = 1000;
        ObjVehicle[2] = new ObjVehicle(gp, 1, 1.5); ObjVehicle[2].worldX = 1800; ObjVehicle[2].worldY = 750;
        ObjVehicle[3] = new ObjVehicle(gp, 2, 1.5); ObjVehicle[3].worldX = 905;  ObjVehicle[3].worldY = 900;

        ObjWoods[0] = new ObjWoods(gp, 1.5); ObjWoods[0].worldX = 2000; ObjWoods[0].worldY = 250;
        ObjWoods[1] = new ObjWoods(gp, 1.5); ObjWoods[1].worldX = 1500; ObjWoods[1].worldY = 1900;
        ObjWoods[2] = new ObjWoods(gp, 1.5); ObjWoods[2].worldX = 600;  ObjWoods[2].worldY = 1100;
        ObjWoods[3] = new ObjWoods(gp, 1.5); ObjWoods[3].worldX = 1000; ObjWoods[3].worldY = 900;

        FireCamp[0] = new FireCamp(gp, 1.5); FireCamp[0].worldX = 800;  FireCamp[0].worldY = 500;
        FireCamp[1] = new FireCamp(gp, 1.5); FireCamp[1].worldX = 1900; FireCamp[1].worldY = 400;

        ObjPineTree[0] = new ObjPineTree(gp, 2); ObjPineTree[0].worldX = 1600; ObjPineTree[0].worldY = 220;
        ObjPineTree[1] = new ObjPineTree(gp, 2); ObjPineTree[1].worldX = 2000; ObjPineTree[1].worldY = 450;
        ObjPineTree[2] = new ObjPineTree(gp, 2); ObjPineTree[2].worldX = 600;  ObjPineTree[2].worldY = 1100;

        ObjHouse[0] = new ObjHouse(gp, 2.5);
        ObjHouse[0].worldX = 1300;
        ObjHouse[0].worldY = 850;

        // --- Riddle tablets — randomized each session ---
        // Map is ~2880x2160. Keep away from: house (1300,850), edges (<200), tree wall (y>1850).
        // Divide map into 3 zones so riddles spread out and don't overlap each other.
        int[][] riddleZones = {
            { 250,  800,  250,  750},   // Zone 0: top-left quadrant
            {1500, 2600,  250,  800},   // Zone 1: top-right quadrant
            { 400, 2400, 1000, 1750},   // Zone 2: middle/bottom (avoids house ~1300,850)
        };
        for (int i = 0; i < 3; i++) {
            int minX = riddleZones[i][0], maxX = riddleZones[i][1];
            int minY = riddleZones[i][2], maxY = riddleZones[i][3];
            int rx, ry;
            // Retry up to 10 times to avoid spawning inside the house area
            int attempts = 0;
            do {
                rx = minX + rand.nextInt(maxX - minX);
                ry = minY + rand.nextInt(maxY - minY);
                attempts++;
            } while (attempts < 10
                  && Math.abs(rx - 1300) < 300
                  && Math.abs(ry - 850)  < 250);
            riddleObjects[i] = new ObjRiddle(gp, i);
            riddleObjects[i].worldX = rx;
            riddleObjects[i].worldY = ry;
        }

        // --- Portal — randomized, placed far from house and riddles ---
        int px, py;
        int portalAttempts = 0;
        do {
            px = 1600 + rand.nextInt(1000);   // x: 1600–2600
            py =  200 + rand.nextInt(600);    // y:  200–800
            portalAttempts++;
        } while (portalAttempts < 20
              && Math.abs(px - 1300) < 300
              && Math.abs(py - 850)  < 250);
        portal = new ObjPortal(px, py);
    }

    public void spawnCollectibles() {
        java.util.Random rand = new java.util.Random();
        int appleIndex = 0;
        int[] offsets = {-30, 0, 30};
        for (int t = 0; t < objAppleTree.length; t++) {
            if (objAppleTree[t] == null) continue;
            if (appleIndex >= appleItems.length) break;
            for (int a = 0; a < 2; a++) {
                if (appleIndex >= appleItems.length) break;
                appleItems[appleIndex] = new ObjAppleItem(gp);
                appleItems[appleIndex].worldX = objAppleTree[t].worldX + offsets[rand.nextInt(offsets.length)] + rand.nextInt(20) - 10;
                appleItems[appleIndex].worldY = objAppleTree[t].worldY + 80 + rand.nextInt(20);
                appleIndex++;
            }
        }
        int[][] woodPositions = {
            {400, 400}, {800, 800}, {1200, 600}, {1600, 900}, {2200, 400},
            {500, 1100}, {1000, 1400}, {1800, 1100}, {2400, 800}, {300, 700},
            {1400, 400}, {900, 500}, {1700, 1500}, {600, 900}, {2100, 1000}
        };
        for (int i = 0; i < woodPositions.length && i < woodItems.length; i++) {
            woodItems[i] = new ObjWoodItem(gp);
            woodItems[i].worldX = woodPositions[i][0] + rand.nextInt(60) - 30;
            woodItems[i].worldY = woodPositions[i][1] + rand.nextInt(60) - 30;
        }
    }

    public void respawnResources() {
        for (int i = 0; i < appleItems.length; i++) {
            if (appleItems[i] != null) appleItems[i].collected = false;
        }
        for (int i = 0; i < woodItems.length; i++) {
            if (woodItems[i] != null) woodItems[i].collected = false;
        }
    }

    public void draw(Graphics2D g2) {

        boolean isNight = gp.dC.currentState == dayCounter.dayNightState.Night;

        if (gp.tileM.currentMap == 1) {

            for (int i = 0; i < objects.length; i++) {
                if (objects[i] == null) continue;
                int screenX = objects[i].worldX - gp.player.worldX + gp.player.screenX;
                int screenY = objects[i].worldY - gp.player.worldY + gp.player.screenY;
                BufferedImage img = (isNight && objects[i].nightImage != null) ? objects[i].nightImage : objects[i].image;
                g2.drawImage(img, screenX, screenY, null);
            }

            for (int i = 0; i < objAppleTree.length; i++) {
                if (objAppleTree[i] == null) continue;
                int screenX = objAppleTree[i].worldX - gp.player.worldX + gp.player.screenX;
                int screenY = objAppleTree[i].worldY - gp.player.worldY + gp.player.screenY;
                BufferedImage img = (isNight && objAppleTree[i].nightImage != null) ? objAppleTree[i].nightImage : objAppleTree[i].image;
                g2.drawImage(img, screenX, screenY, null);
            }

            for (int i = 0; i < ObjVehicle.length; i++) {
                if (ObjVehicle[i] == null) continue;
                int screenX = ObjVehicle[i].worldX - gp.player.worldX + gp.player.screenX;
                int screenY = ObjVehicle[i].worldY - gp.player.worldY + gp.player.screenY;
                BufferedImage img = (isNight && ObjVehicle[i].nightImage != null) ? ObjVehicle[i].nightImage : ObjVehicle[i].image;
                g2.drawImage(img, screenX, screenY, null);
            }

            for (int i = 0; i < ObjHouse.length; i++) {
                if (ObjHouse[i] == null) continue;
                int screenX = ObjHouse[i].worldX - gp.player.worldX + gp.player.screenX;
                int screenY = ObjHouse[i].worldY - gp.player.worldY + gp.player.screenY;
                ((ObjHouse) ObjHouse[i]).draw(g2, screenX, screenY);
            }

            for (int i = 0; i < ObjWoods.length; i++) {
                if (ObjWoods[i] == null) continue;
                int screenX = ObjWoods[i].worldX - gp.player.worldX + gp.player.screenX;
                int screenY = ObjWoods[i].worldY - gp.player.worldY + gp.player.screenY;
                BufferedImage img = (isNight && ObjWoods[i].nightImage != null) ? ObjWoods[i].nightImage : ObjWoods[i].image;
                g2.drawImage(img, screenX, screenY, null);
            }

            for (int i = 0; i < FireCamp.length; i++) {
                if (FireCamp[i] == null) continue;
                int screenX = FireCamp[i].worldX - gp.player.worldX + gp.player.screenX;
                int screenY = FireCamp[i].worldY - gp.player.worldY + gp.player.screenY;
                BufferedImage img = (isNight && FireCamp[i].nightImage != null) ? FireCamp[i].nightImage : FireCamp[i].image;
                g2.drawImage(img, screenX, screenY, null);
            }

            for (int i = 0; i < ObjPineTree.length; i++) {
                if (ObjPineTree[i] == null) continue;
                int screenX = ObjPineTree[i].worldX - gp.player.worldX + gp.player.screenX;
                int screenY = ObjPineTree[i].worldY - gp.player.worldY + gp.player.screenY;
                BufferedImage img = (isNight && ObjPineTree[i].nightImage != null) ? ObjPineTree[i].nightImage : ObjPineTree[i].image;
                g2.drawImage(img, screenX, screenY, null);
            }

            for (int i = 0; i < ObjTreeTop.length; i++) {
                if (ObjTreeTop[i] == null) continue;
                int screenX = ObjTreeTop[i].worldX - gp.player.worldX + gp.player.screenX;
                int screenY = ObjTreeTop[i].worldY - gp.player.worldY + gp.player.screenY;
                g2.drawImage(ObjTreeTop[i].image, screenX, screenY, null);
            }

            for (int i = 0; i < ObjTreeTopBR.length; i++) {
                if (ObjTreeTopBR[i] == null) continue;
                int screenX = ObjTreeTopBR[i].worldX - gp.player.worldX + gp.player.screenX;
                int screenY = ObjTreeTopBR[i].worldY - gp.player.worldY + gp.player.screenY;
                g2.drawImage(ObjTreeTopBR[i].image, screenX, screenY, null);
            }

            for (int i = 0; i < appleItems.length; i++) {
                if (appleItems[i] == null) continue;
                if (appleItems[i].collected) continue;
                int screenX = appleItems[i].worldX - gp.player.worldX + gp.player.screenX;
                int screenY = appleItems[i].worldY - gp.player.worldY + gp.player.screenY;
                BufferedImage img = (isNight && appleItems[i].nightImage != null) ? appleItems[i].nightImage : appleItems[i].image;
                g2.drawImage(img, screenX, screenY, null);
            }

            for (int i = 0; i < woodItems.length; i++) {
                if (woodItems[i] == null) continue;
                if (woodItems[i].collected) continue;
                int screenX = woodItems[i].worldX - gp.player.worldX + gp.player.screenX;
                int screenY = woodItems[i].worldY - gp.player.worldY + gp.player.screenY;
                BufferedImage img = (isNight && woodItems[i].nightImage != null) ? woodItems[i].nightImage : woodItems[i].image;
                g2.drawImage(img, screenX, screenY, null);
            }

            // --- Riddle tablets ---
            for (int i = 0; i < riddleObjects.length; i++) {
                if (riddleObjects[i] == null) continue;
                ObjRiddle rt = (ObjRiddle) riddleObjects[i];
                int screenX = rt.worldX - gp.player.worldX + gp.player.screenX;
                int screenY = rt.worldY - gp.player.worldY + gp.player.screenY;
                BufferedImage img = (isNight && rt.nightImage != null) ? rt.nightImage : rt.image;
                g2.drawImage(img, screenX, screenY, null);

                // Green glow badge when solved
                if (gp.riddleM.getRiddle(rt.riddleIndex) != null
                        && gp.riddleM.getRiddle(rt.riddleIndex).solved) {
                    g2.setColor(new java.awt.Color(80, 200, 90, 160));
                    g2.fillOval(screenX + 14, screenY - 6, 12, 12);
                }
            }

            // --- Portal (visible only after all riddles solved) ---
            if (portalVisible) {
                int screenX = portal.worldX - gp.player.worldX + gp.player.screenX;
                int screenY = portal.worldY - gp.player.worldY + gp.player.screenY;
                portal.draw(g2, screenX, screenY);
            }
        }

        if (gp.tileM.currentMap == 2) {
            interior.draw(g2, 0, 0, (models.ObjHouse) ObjHouse[0]);
        }
    }
}
