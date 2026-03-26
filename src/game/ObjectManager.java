package game;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import models.GameObject;
import models.ObjAppleItem;
import models.ObjAppleTree;
import models.ObjExternalObjects;
import models.ObjHouse;
import models.ObjInterior;
import models.ObjPortal;
import models.ObjRiddle;
import models.ObjStone;
import models.ObjTree;
import models.ObjTreeTop;
import models.ObjTreeTopBR;
import models.ObjWoodItem;

public class ObjectManager {

    panel gp;
    public GameObject[] objects;
    public GameObject[] objAppleTree;
    public GameObject[] ObjHouse;
    public GameObject[] ObjTreeTop;
    public GameObject[] ObjTreeTopBR;
    public ObjInterior interior;
    public GameObject[] appleItems;
    public GameObject[] woodItems;
    public GameObject houseStone;
    public GameObject[] riddleObjects;
    public GameObject[] extObjects;
    public GameObject[] ObjUndergroundShelter;

    public ObjPortal portal;
    public boolean portalVisible = false;

    public ObjectManager(panel gp) {

        this.gp = gp;
        objects = new GameObject[5900];
        objAppleTree = new GameObject[100];
        ObjHouse = new GameObject[10];
        ObjTreeTop = new GameObject[100];
        ObjTreeTopBR = new GameObject[100];
        interior = new models.ObjInterior(gp);
        appleItems = new GameObject[50];
        woodItems = new GameObject[50];
        riddleObjects = new GameObject[3];
        extObjects = new GameObject[200];
        ObjUndergroundShelter = new GameObject[3];
        setObjects();
        spawnCollectibles();
    }

    public void revealPortal() {
        portalVisible = true;
    }

    public void update() {

        if (gp.tileM.currentMap == 2) {
            interior.update();
        }

        if (gp.tileM.currentMap == 1 && portalVisible) {
            portal.update();
        }
    }

    public void setObjects() {

        java.util.Random rand = new java.util.Random();

        // Top tree rows
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

        // Left tree wall
        int indexLeft = 300;
        int startX = -100;
        int startY = 119;
        int rows = 30;
        int cols = 10;
        int spacingX = 45;
        int spacingY = 60;

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

        // Right tree wall
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

        // Bottom tree rows
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

        // Tree tops left
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

        // Tree tops right
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

        // Apple trees 
        objAppleTree[0] = new ObjAppleTree(gp, 2.7); objAppleTree[0].worldX = 496; objAppleTree[0].worldY = 748; //left side ng stone
        objAppleTree[1] = new ObjAppleTree(gp, 2.7); objAppleTree[1].worldX = 990; objAppleTree[1].worldY = 748; //right side ng stones
        objAppleTree[2] = new ObjAppleTree(gp, 2.7); objAppleTree[2].worldX = 520; objAppleTree[2].worldY = 1048; // left side other road
        objAppleTree[3] = new ObjAppleTree(gp, 2.7); objAppleTree[3].worldX = 1147; objAppleTree[3].worldY = 248; //right side ng likod ng haws
        objAppleTree[7] = new ObjAppleTree(gp, 2.7); objAppleTree[7].worldX = 1147; objAppleTree[7].worldY = 320; //right side 2nd sa likod ng haws
        objAppleTree[8] = new ObjAppleTree(gp, 2.7); objAppleTree[8].worldX = 1147; objAppleTree[8].worldY = 374; //right side 3nd sa likod ng haws
        objAppleTree[4] = new ObjAppleTree(gp, 2.7); objAppleTree[4].worldX = 1147; objAppleTree[4].worldY = 538; //right side ng house
        objAppleTree[5] = new ObjAppleTree(gp, 2.7); objAppleTree[5].worldX = 1082; objAppleTree[5].worldY = 668; //right side 2nd after ng katabi ng stone
        objAppleTree[6] = new ObjAppleTree(gp, 2.7); objAppleTree[6].worldX = 505; objAppleTree[6].worldY = 540; //left side 2nd after ng katabi ng stone
        


        
        
        // House + stone
        houseStone = new ObjStone(gp, 1.3);
        houseStone.worldX = 620;
        houseStone.worldY = 535;

        ObjHouse[0] = new ObjHouse(gp, 2.5);
        ObjHouse[0].worldX = 644;
        ObjHouse[0].worldY = 530;

        int ei = 0;
        
        // aayusin pa position ng ibang objects. pero yung stones and ruins okay na cguro.

        // Dead trees 
        extObjects[ei] = new ObjExternalObjects(gp, "deadTree1", 2.5); extObjects[ei].worldX = 1552; extObjects[ei].worldY = 1499; ei++;
        extObjects[ei] = new ObjExternalObjects(gp, "deadTree2", 2.5); extObjects[ei].worldX = 936; extObjects[ei].worldY = 1601; ei++;
        extObjects[ei] = new ObjExternalObjects(gp, "deadTree1", 2.5); extObjects[ei].worldX = 996; extObjects[ei].worldY = 1448; ei++; // 2nd lowest
        extObjects[ei] = new ObjExternalObjects(gp, "deadTree2", 2.5); extObjects[ei].worldX = 1500; extObjects[ei].worldY = 1024; ei++;
        extObjects[ei] = new ObjExternalObjects(gp, "deadTree1", 2.5); extObjects[ei].worldX = 1200; extObjects[ei].worldY = 1092; ei++;
        extObjects[ei] = new ObjExternalObjects(gp, "deadTree2", 2.5); extObjects[ei].worldX = 1113; extObjects[ei].worldY = 1598; ei++;//lowest

        // Broken tree
        extObjects[ei] = new ObjExternalObjects(gp, "brokenTree", 2.5); extObjects[ei].worldX = 740; extObjects[ei].worldY = 1364; ei++;

        
        // Ruins
        extObjects[ei] = new ObjExternalObjects(gp, "ruinFourPillar", 2.5); extObjects[ei].worldX = 1688; extObjects[ei].worldY = 324; ei++;
        extObjects[ei] = new ObjExternalObjects(gp, "ruinFourPillar", 2.5); extObjects[ei].worldX = 1924; extObjects[ei].worldY = 364; ei++;
        extObjects[ei] = new ObjExternalObjects(gp, "ruinArch", 2.5); extObjects[ei].worldX = 1836; extObjects[ei].worldY = 550; ei++;
        extObjects[ei] = new ObjExternalObjects(gp, "ruinWall", 2.5); extObjects[ei].worldX = 1840; extObjects[ei].worldY = 788; ei++;
        extObjects[ei] = new ObjExternalObjects(gp, "ruinPillar", 2.5); extObjects[ei].worldX = 1712; extObjects[ei].worldY = 752; ei++;
        extObjects[ei] = new ObjExternalObjects(gp, "ruinPillar", 2.5); extObjects[ei].worldX = 2100; extObjects[ei].worldY = 672; ei++;
        extObjects[ei] = new ObjExternalObjects(gp, "ruinPebbles", 2.5); extObjects[ei].worldX = 1804; extObjects[ei].worldY = 596; ei++;
        extObjects[ei] = new ObjExternalObjects(gp, "ruinPebbles", 2.5); extObjects[ei].worldX = 1820; extObjects[ei].worldY = 708; ei++;
        extObjects[ei] = new ObjExternalObjects(gp, "ruinPebbles", 2.5); extObjects[ei].worldX = 1872; extObjects[ei].worldY = 616; ei++;
        extObjects[ei] = new ObjExternalObjects(gp, "ruinPebbles", 2.5); extObjects[ei].worldX = 2064; extObjects[ei].worldY = 768; ei++;

        // Rocks 
        extObjects[ei] = new ObjExternalObjects(gp, "rockBig", 2.5); extObjects[ei].worldX = 1550; extObjects[ei].worldY = 1300; ei++;
        extObjects[ei] = new ObjExternalObjects(gp, "rockMedium", 2.5); extObjects[ei].worldX = 800; extObjects[ei].worldY = 1700; ei++;
        extObjects[ei] = new ObjExternalObjects(gp, "rockSmall", 2.5); extObjects[ei].worldX = 1229; extObjects[ei].worldY = 886; ei++;
        extObjects[ei] = new ObjExternalObjects(gp, "rockXS", 2.5); extObjects[ei].worldX = 1200; extObjects[ei].worldY = 800; ei++;
        extObjects[ei] = new ObjExternalObjects(gp, "rockMedium", 2.5); extObjects[ei].worldX = 2204; extObjects[ei].worldY = 1204; ei++;
        extObjects[ei] = new ObjExternalObjects(gp, "rockSmall", 2.5); extObjects[ei].worldX = 1800; extObjects[ei].worldY = 1500; ei++;
        extObjects[ei] = new ObjExternalObjects(gp, "rockXS", 2.5); extObjects[ei].worldX = 700; extObjects[ei].worldY = 400; ei++;

        // Thorns
        extObjects[ei] = new ObjExternalObjects(gp, "thornBig", 2.5); extObjects[ei].worldX = 788; extObjects[ei].worldY = 380; ei++;
        extObjects[ei] = new ObjExternalObjects(gp, "thornMedium", 2.5); extObjects[ei].worldX = 1100; extObjects[ei].worldY = 1600; ei++;
        extObjects[ei] = new ObjExternalObjects(gp, "thornSmall", 2.5); extObjects[ei].worldX = 2500; extObjects[ei].worldY = 800; ei++;
        extObjects[ei] = new ObjExternalObjects(gp, "thornBig", 2.5); extObjects[ei].worldX = 400; extObjects[ei].worldY = 1700; ei++;
        extObjects[ei] = new ObjExternalObjects(gp, "thornSmall", 2.5); extObjects[ei].worldX = 1400; extObjects[ei].worldY = 300; ei++;

        // Graves
        extObjects[ei] = new ObjExternalObjects(gp, "leftGrave1", 2.5); extObjects[ei].worldX = 464; extObjects[ei].worldY = 276; ei++;
        extObjects[ei] = new ObjExternalObjects(gp, "leftGrave2", 2.5); extObjects[ei].worldX = 444; extObjects[ei].worldY = 336; ei++;
        extObjects[ei] = new ObjExternalObjects(gp, "leftGrave2", 2.5); extObjects[ei].worldX = 565; extObjects[ei].worldY = 260; ei++;
        extObjects[ei] = new ObjExternalObjects(gp, "rightGrave", 2.5); extObjects[ei].worldX = 460; extObjects[ei].worldY = 1500; ei++;
        //extObjects[ei] = new ObjExternalObjects(gp, "pileSkull", 2.5); extObjects[ei].worldX = 380; extObjects[ei].worldY = 1600; ei++;

        // Plants 
        extObjects[ei] = new ObjExternalObjects(gp, "plantBig", 2.5); extObjects[ei].worldX = 550; extObjects[ei].worldY = 300; ei++;
        extObjects[ei] = new ObjExternalObjects(gp, "plantMedium", 2.5); extObjects[ei].worldX = 1300; extObjects[ei].worldY = 1500; ei++;
        extObjects[ei] = new ObjExternalObjects(gp, "plantSmall", 2.5); extObjects[ei].worldX = 2200; extObjects[ei].worldY = 1300; ei++;
        extObjects[ei] = new ObjExternalObjects(gp, "plantMedium", 2.5); extObjects[ei].worldX = 800; extObjects[ei].worldY = 1800; ei++;

        // Green plants
        extObjects[ei] = new ObjExternalObjects(gp, "greenPlantBig", 2.5); extObjects[ei].worldX = 1500; extObjects[ei].worldY = 1700; ei++;
        extObjects[ei] = new ObjExternalObjects(gp, "greenPlantMedium", 2.5); extObjects[ei].worldX = 600; extObjects[ei].worldY = 1750; ei++;
        extObjects[ei] = new ObjExternalObjects(gp, "greenPlantSmall", 2.5); extObjects[ei].worldX = 2100; extObjects[ei].worldY = 1500; ei++;

        // Underground shelter
        int[][] shelterSpawnSpots = {
            
            {1236, 1824}, {520, 890}, {480, 1544},
            {2260, 1308}, {1492, 248}, {464, 248},
            {2086, 608}, {1640, 1254}, {2324, 252},
            {1196, 376}, {1960, 732}, {1252, 1196}
        };

        ArrayList<Integer> usedShelterIndices = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            
            int spotIndex;
            int attempts = 0;

            do {
                
                spotIndex = rand.nextInt(shelterSpawnSpots.length);
                attempts++;

                if (attempts > 100) break;

            } while (usedShelterIndices.contains(spotIndex));

            usedShelterIndices.add(spotIndex);
            int[] spot = shelterSpawnSpots[spotIndex];

            ObjUndergroundShelter[i] = new models.ObjUndergroundShelter(gp);
            ObjUndergroundShelter[i].worldX = spot[0];
            ObjUndergroundShelter[i].worldY = spot[1];
        }
        
        
        // Riddles
        int[][] riddleSpawnSpots = {
            
            {1500, 650}, 
            {920, 1150},
            {1476, 1333}, 
            {1070, 1200},
            {1070, 1470}, 
            
            // Ruins area
            {1550, 350}, //west
            {1800, 480}, //south
            {1650, 720}, //soutwest
            {1950, 720}, // near pillar
            
            // Right side woods
            {2150, 350}, //east
            {2340, 500}, //far-east
            {2250, 850}, //southeast
            
            // Lower areas
            {750, 1250}, //south
            {1100, 1350}, // Southeast 
            {550, 1550}, //southwest
            
            {620, 300}, 
            {970, 950}, 
            {850, 1320} 
        };

        // randomly select 3 spots sa fixed locations
        ArrayList<Integer> usedIndices = new ArrayList<>();
        
        for (int i = 0; i < 3; i++) {
            int spotIndex;
            int attempts = 0;
            
            do {
                spotIndex = rand.nextInt(riddleSpawnSpots.length);
                attempts++;
                
                if (attempts > 100) break;
                
            } while (usedIndices.contains(spotIndex));
            
            usedIndices.add(spotIndex);
            int[] spot = riddleSpawnSpots[spotIndex];
            
            riddleObjects[i] = new ObjRiddle(gp, i);
            riddleObjects[i].worldX = spot[0];
            riddleObjects[i].worldY = spot[1];
            
        }

        // Portal
        int px, py;
        int portalAttempts = 0;

        do {

            px = 1600 + rand.nextInt(1000);
            py = 200 + rand.nextInt(600);
            portalAttempts++;

        } while (portalAttempts < 20 && Math.abs(px - 644) < 300 && Math.abs(py - 530) < 250);

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
    
    public void respawnByDay(int day) {

        // Restore all first
        respawnResources();

        // Then hide a portion based on day
        float hideChance;
        if (day == 2)      hideChance = 0.40f; // 40% hidden on Day 2
        else if (day >= 3) hideChance = 0.70f; // 70% hidden on Day 3
        else               return;             // Day 1 = full resources

        java.util.Random rand = new java.util.Random();

        for (int i = 0; i < appleItems.length; i++) {
            if (appleItems[i] != null && rand.nextFloat() < hideChance) {
                appleItems[i].collected = true;
            }
        }

        for (int i = 0; i < woodItems.length; i++) {
            if (woodItems[i] != null && rand.nextFloat() < hideChance) {
                woodItems[i].collected = true;
            }
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

            for (int i = 0; i < extObjects.length; i++) {

                if (extObjects[i] == null) continue;

                int screenX = extObjects[i].worldX - gp.player.worldX + gp.player.screenX;
                int screenY = extObjects[i].worldY - gp.player.worldY + gp.player.screenY;

                ((ObjExternalObjects) extObjects[i]).draw(g2, screenX, screenY);
            }

            if (houseStone != null) {

                int screenX = houseStone.worldX - gp.player.worldX + gp.player.screenX;
                int screenY = houseStone.worldY - gp.player.worldY + gp.player.screenY;

                BufferedImage img = (isNight && houseStone.nightImage != null) ? houseStone.nightImage : houseStone.image;
                g2.drawImage(img, screenX, screenY, null);
            }

            for (int i = 0; i < ObjHouse.length; i++) {

                if (ObjHouse[i] == null) continue;

                int screenX = ObjHouse[i].worldX - gp.player.worldX + gp.player.screenX;
                int screenY = ObjHouse[i].worldY - gp.player.worldY + gp.player.screenY;

                ((ObjHouse) ObjHouse[i]).draw(g2, screenX, screenY);
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

            for (int i = 0; i < riddleObjects.length; i++) {

                if (riddleObjects[i] == null) continue;

                ObjRiddle rt = (ObjRiddle) riddleObjects[i];

                int screenX = rt.worldX - gp.player.worldX + gp.player.screenX;
                int screenY = rt.worldY - gp.player.worldY + gp.player.screenY;

                BufferedImage img = (isNight && rt.nightImage != null) ? rt.nightImage : rt.image;
                g2.drawImage(img, screenX, screenY, null);

                if (gp.riddleM.getRiddle(rt.riddleIndex) != null && gp.riddleM.getRiddle(rt.riddleIndex).solved) {

                    g2.setColor(new java.awt.Color(80, 200, 90, 160));
                    g2.fillOval(screenX + 14, screenY - 6, 12, 12);
                }
            }

            if (portalVisible) {

                int screenX = portal.worldX - gp.player.worldX + gp.player.screenX;
                int screenY = portal.worldY - gp.player.worldY + gp.player.screenY;

                portal.draw(g2, screenX, screenY);
            }
            
            for (int i = 0; i < ObjUndergroundShelter.length; i++) {
                
            if (ObjUndergroundShelter[i] == null){
                continue;
            }

            int screenX = ObjUndergroundShelter[i].worldX - gp.player.worldX + gp.player.screenX;
            int screenY = ObjUndergroundShelter[i].worldY - gp.player.worldY + gp.player.screenY;

            ((models.ObjUndergroundShelter) ObjUndergroundShelter[i]).draw(g2, screenX, screenY);
        }

        }

        if (gp.tileM.currentMap == 2) {

            interior.draw(g2, 0, 0, (models.ObjHouse) ObjHouse[0]);
        }
    }
}
