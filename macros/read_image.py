from PIL import Image, PixarImagePlugin
import os,sys
from array import array
from ROOT import TFile, TTree
import math
import os.path
import locale
#from dialog import Dialog

def Log(sr):
    if(sr == 0):
        return 0
    elif(sr < 0):
        return 0
    else:
        return math.log(sr)

def GetMax(rgb):
    maxmin = [0,0,0]
    if(rgb[0] > rgb[1] and rgb[0] > rgb[2]):
        maxmin[0] = rgb[0]
    elif(rgb[1] > rgb[0] and rgb[1] > rgb[2]):
        maxmin[0] = rgb[1]
    elif(rgb[2] > rgb[0] and rgb[2] > rgb[1]):
        maxmin[0] = rgb[2]
    else:
        maxmin[0] = rgb[0]
    if(rgb[0] < rgb[1] and rgb[0] < rgb[2]):
        maxmin[1] = rgb[0]
    elif(rgb[1] < rgb[0] and rgb[1] < rgb[2]):
        maxmin[1] = rgb[1]
    elif(rgb[2] < rgb[0] and rgb[2] < rgb[1]):
        maxmin[1] = rgb[2]
    else:
        maxmin[1] = rgb[0]
    maxmin[2] = maxmin[0] - maxmin[1]
    return maxmin

def GetHue(maxi, rgb):
    if(maxi[2] == 0):
        return 0
    if(maxi[0] == rgb[0]):
        return 60*((abs(rgb[1]-rgb[2])/maxi[2])%6)
    if(maxi[0] == rgb[1]):
        return 60*(abs(rgb[2]-rgb[0])/maxi[2]+2)
    if(maxi[0] == rgb[2]):
        return 60*(abs(rgb[0]-rgb[1])/maxi[2]+4)
    return -1

def GetRGBp(rgb):
    rgbp = [0,0,0]
    rgbp[0] = rgb[0]/255
    rgbp[1] = rgb[1]/255
    rgbp[2] = rgb[2]/255
    return rgbp

def GetBrightness(rgb):
    return (rgb[0] + rgb[1] + rgb[2])/3

def GetSaturation(maxmin):
    if(maxmin[0] == 0 ):
        return 0
    else:
        return maxmin[2]/maxmin[0]

def getRGBMean(R,G,B):
    return (10*R + 10*G + B)/3

def ImageCrop(im):
    cropSpan = 50
    searchSpan = 2
    # Find the center ofthe rectangle to crop
    center = ( 0,0 )
    maxmean = 0
    maxi = 0;
    maxj = 0;
    pix = im.load()
    for ii in range(searchSpan, im.size[0] - searchSpan):
        for jj in range(searchSpan, im.size[1] - searchSpan):
 
            # span over a few neightbors now
            regionR = 0
            regionG = 0
            regionB = 0
            for i in range(ii - searchSpan, ii + searchSpan):
                for j in range(jj - searchSpan, jj + searchSpan):
                    regionR += pix[i, j][0]
                    regionG += pix[i, j][1]
                    regionB += pix[i, j][2]
                    
            mean = getRGBMean(regionR,regionG,regionB)
            if( mean > maxmean ):
                maxmean = mean
                maxi = ii
                maxj = jj
    
    cropRect = ( maxi - cropSpan, maxj - cropSpan, maxi + cropSpan, maxj + cropSpan )
    #cropRect = ( 0,0,100,100 )
    
    # make a crop
    return im.crop( cropRect )
        

def GetDic(labels):
    dic = {}
    labf = open(labels, 'r')
    for item in labf:
        #print item
        if(item != 'image,level'):
            sta = item.rsplit(',')
            dic[sta[0]] = sta[1].rsplit('\n')[0]
    labf.close()
    return dic

if __name__ == "__main__":

    #Folder = '/home/aocampor/DiabeticRetinophaty/sample/'
    #Folder = '/media/aocampor/MyDisk/DiabeticRetinophaty/'
    #fols = ['0Fold','1Fold','2Fold','3Fold','4Fold']
    Folder = str(sys.argv[1])
    files = []
    #for item in fols:
        #fi = os.listdir(Folder+item+'/') 
    fi = os.listdir(Folder+'/') 
    for ix in fi:
        files.append( ix )

    #print files
    #labelfile = '/home/aocampor/DiabeticRetinophaty/trainLabels.csv'
    labelfile = str(sys.argv[2])    

    outdir = str(sys.argv[3])
    
    if(not os.path.isdir(outdir)):
        os.system('mkdir ' + outdir)

    dic = GetDic(labelfile)

    for item in files:
        
        item1 = 'Lowres_' + item

        token = item.rsplit('.jpeg')
        #print item, token
        level = array('i', [0])

        level[0] = int(dic[token[0]])

        #print("%s | level: %d"% (token, level[0]))
        
        inputs = Folder + '/' + item
        #output = Folder + fols[ int(dic[token[0]]) ] + 'LowRes/' + item1
        if(not os.path.isdir(outdir + 'mod/')):
            os.system('mkdir ' + outdir + 'mod/')
            os.system('mkdir ' + outdir + 'mod/' + str(level[0]))
        output = outdir + '/mod/' + str(level[0]) + '/' + item1
        if(not os.path.isfile(output) ):
            command = 'convert  -resize 5% ' + inputs + ' ' + output
            os.system(command)
        rootname = outdir + '/mod/' + str(level[0]) + '/Image' + token[0] + '.root'    
        if(os.path.isfile(rootname)):
            continue
        rootfil = TFile(rootname, 'recreate')
        tree = TTree('Images', 'Images')
        colorr = array('d', [0])
        colorg = array('d', [0])
        colorb = array('d', [0])
        hue = array('d', [0])
        bright = array('d', [0])
        sat = array('d', [0])
        #colorgor = array('d', [0])
        #colorbor = array('d', [0])
        #level = array('i', [0])
        
        #level[0] = int(dic[token[0]])
        
        tree.Branch('Red', colorr, 'Red/D')
        tree.Branch('Green', colorg, 'Green/D')
        tree.Branch('Blue', colorb, 'Blue/D')
        tree.Branch('Hue', hue, 'Hue/D')
        tree.Branch('Bright', bright, 'Bright/D')
        tree.Branch('Saturation', sat, 'Saturation/D')
        #tree.Branch('GreenOverRed', colorgor, 'GreenOverRed/D')
        #tree.Branch('BlueOverRed', colorbor, 'BlueOverRed/D')
        tree.Branch('Level', level, 'Level/I')

        # just a dialogue to interact with this
        #d = Dialog(dialog="dialog")
        #d.set_background_title("Diabetes")

        #print(item)
        imOriginal10p = Image.open(output)  # Can be many different formats.
        #im.rotate(0).show()
        
        # Selection
        im = ImageCrop(imOriginal10p)
        #pid = im.rotate(0).show()
        #if d.yesno("Continue?") == 0:
        #    print pid
        #else:
        #    sys.exit(0)
                
        # work on the crop
        pix = im.load()
        
        #pixg = im.convert('LA').load()

        for i in range(im.size[0]):
            for j in range(im.size[1]):
                if(pix[i, j][0] <= 3 and pix[i, j][1] <= 3 and
                   pix[i, j][2] <= 3):
                    continue
                colorr[0] = pix[i, j][0]
                colorg[0] = pix[i, j][1]
                colorb[0] = pix[i, j][2]
                rgb = [colorr[0],colorg[0],colorb[0]]
                rgbp = GetRGBp(rgb)
                maxmin = GetMax(rgbp)
                hue[0] = GetHue(maxmin, rgbp)
                bright[0] = GetBrightness(rgb)
                sat[0] = GetSaturation(maxmin)
                #if(colorr[0] != 0):
                #    colorbor[0] = colorb[0]/colorr[0]
                #    colorgor[0] = colorg[0]/colorr[0]
                #else:
                #    colorbor[0] = 0
                #    colorgor[0] = 0
                tree.Fill()

        rootfil.Write()
        rootfil.Close()

