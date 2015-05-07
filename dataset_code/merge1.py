__author__ = 'celia'

import csv
import numpy
import pdb

f1 = open ('School_Progress_Report_2009-2010.csv', 'rt')
f2 = open('NYS_Math_Test_Results_By_Grade_2006-2011_-_School_Level_-_All_Students.csv', 'rt')
f3 = open('INTEGRATED-DATASET_1.csv', 'wt')
try:
    reader1 = csv.reader(f1)
    reader2 = csv.reader(f2)
    writer = csv.writer(f3)

    reader1 = list(reader1)
    reader2 = list(reader2)

    writer.writerow((reader1[0][0], reader1[0][1], reader1[0][5], "SCHOOL OVERALL GRADE", "SCHOOL ENVIRONMENT GRADE", \
                     "SCHOOL PERFORMANCE GRADE","SCHOOL PROGRESS GRADE", "MATH MEAN SCALE SCORE GRADE", "MATH SCORE LEVEL 3 and LEVEL 4 PCT DENSITY"))



    for i in range(1, len(reader1)):
        mathmean = []
        pctlev34 = []
        data1 = reader1[i][0]
        data2 = reader1[i][1]
        data3 = reader1[i][5]
        data4 = reader1[i][7]
        data5 = reader1[i][10]
        data6 = reader1[i][12]
        data7 = reader1[i][14]

        for j in range(1, len(reader2)):
            if reader2[j][1] != 'All Grades' and (reader2[j][2] == '2009' or reader2[j][2] == '2010') and reader1[i][0] == reader2[j][0]:

                if reader1[i][0] == 's' or reader1[i][1] == 's' or reader1[i][5] == 's' or reader1[i][7] == 's' \
                        or reader1[i][10] == 's' or reader1[i][12] == 's' or reader1[i][14] == 's' \
                        or reader1[i][16] == 's'  \
                        or reader2[j][5] == 's' or reader2[j][15] == 's' or\
                        reader1[i][0] == '' or reader1[i][1] == '' or reader1[i][5] == '' or \
                        reader1[i][7] == '' or reader1[i][10] == '' or reader1[i][12] == '' or \
                        reader1[i][14] == '' or reader1[i][16] == '' or \
                        reader2[j][5] == '' or reader2[j][15] == '':
                    break
                else:

                    # data1 = reader1[i][0]

                    mathmean.append(int(reader2[j][5]))
                    pctlev34.append(float(reader2[j][15].strip(' \t\n\r%')))

        if len(mathmean) > 0 and len(pctlev34) > 0 :
            meanmath = numpy.average(mathmean)

            if meanmath > 687.0:
                mathgrade = "A"
            elif meanmath > 672.0:
                mathgrade = "B"
            else:
                mathgrade = "C"

            # pdb.set_trace()

            meanlev34 = numpy.average(pctlev34)

            if meanlev34 > 78.0:
                level34Pct = "High"
            elif meanlev34 > 62.0:
                level34Pct = "Medium"
            else:
                level34Pct = "Low"

            # pdb.set_trace()
            writer.writerow((data1, data2, data3, data4, data5, data6, data7, mathgrade, level34Pct))


                    #  if float(reader2[j][7].strip(' \t\n\r%')) > 20.0:
                    #     level1Pct = "High"
                    # elif float(reader2[j][7].strip(' \t\n\r%')) > 5.0:
                    #     level1Pct = "Medium"
                    # else:
                    #     level1Pct = "Low"


                    # if float(reader2[j][7].strip(' \t\n\r%')) + float(reader2[j][9].strip(' \t\n\r%')) > 55.0:
                    #     level12Pct = "High"
                    # elif float(reader2[j][7].strip(' \t\n\r%')) + float(reader2[j][9].strip(' \t\n\r%')) > 33.3:
                    #     level12Pct = "Medium"
                    # else:
                    #     level12Pct = "Low"

                    # if float(reader2[j][9].strip(' \t\n\r%')) > 30.0:
                    #     level2Pct = "High"
                    # elif float(reader2[j][9].strip(' \t\n\r%')) > 15.0:
                    #     level2Pct = "Medium"
                    # else:
                    #     level2Pct = "Low"
                    #

finally:
    f1.close()
    f2.close()
    f3.close()
