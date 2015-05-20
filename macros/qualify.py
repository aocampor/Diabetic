from array import array
import os, sys

def GetDic(labels):
    dic = {}
    labf = open(labels, 'r')
    for item in labf:
        if(str(item) != 'image,level\n'):
            sta = item.rsplit(',')
            dic[sta[0]] = sta[1].rsplit('\n')[0]
    labf.close()
    return dic

if __name__ == "__main__":

    labelfile = '/home/aocampor/DiabeticRetinophaty/trainLabels.csv'
    labelfile1 = str(sys.argv[1])
    dic0 = GetDic(labelfile)
    dic1 = GetDic(labelfile1)

    #print dic0
    
    Oij = {}
    wij = {}

    for i in range(0,5):
        for j in range(0,5):
            key = str(i) + ',' + str(j)
            Oij[key] = 0
            wij[key] = (float)((i-j)*(i-j))/16
            


    for item in dic0:
        #print dic0[item] , dic1[item]
        if item in dic1:
            key = dic0[item] + ',' + dic1[item]
        else:
            key = dic0[item] + ',' + str(4)
        #print item, key
        Oij[key] = Oij[key] + 1
        
    suma = 0    
    for i in range(0,5):
        key = str(i) + ',' + str(i)
        suma = suma + Oij[key]

    num = 0
    for i in range(0,5):
        for j in range(0,5):    
            key = str(i) + ',' + str(j)
            num = num + wij[key]*Oij[key]

    den = 0
    for i in range(0,5):
        #for j in range(0,5):    
            key = str(i) + ',' + str(i)
            den = den + (float)(suma)/5

    #print Oij , wij       
    #print suma, num, den
    print 'Our eff so far: ' + str(1 - (float)(num)/den)        
 
