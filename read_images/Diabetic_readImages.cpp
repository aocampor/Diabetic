//============================================================================
// Name        : Diabetic_readImages.cpp
// Author      : 
// Version     :
// Copyright   : Your copyright notice
// Description : Hello World in C++, Ansi-style
//============================================================================

#include <iostream>
#include <string>
#include <fstream>
#include <map>
#include <vector>
#include <thread>
#include <sstream>

#include <stdlib.h>
#include <unistd.h>
#include <stdio.h>
#include <sys/types.h>
#include <dirent.h>
#include <sys/stat.h>

#include "jpeglib.h"
#include <setjmp.h>

#include "TFile.h"
#include "TTree.h"
#include "TString.h"
#pragma link C++ class vector<std::string>;

using namespace std;

#define __trainLabels 		"/home/aocampor/DiabeticRetinophaty/trainLabels.csv"
#define __lenghtprintcsv 10
#define __lowres_percentage 10
#define debug false


typedef struct {
  string data_dir;
  string output_dir;
  int nthreads;
  string fnbit;
  int drlevel;
  TFile * rootfile;
  TTree * roottree;
} input_parameters;

// prototypes
vector<pair<string, int> > readCSVFile(string fn);
void DumpCSVInfo(vector<pair<string, int> > v);
input_parameters InputParameters(int argc, char ** argv);
static const int GetConcurrentThreads();
string LowerRes(string file, double lowrespercentage, string prefix, input_parameters);
void ProcessImages(vector<pair<string, int> > csvinfo, input_parameters);
bool DirectoryExists(string);
void ProcessOneImage(int threadId, input_parameters ip);
void InImageSelection(string fn, JSAMPLE * RGB, int width, int height);
void BuildNtuple(JSAMPLE * RGB, int width_i, int height_i, input_parameters ip);

// libjpeg handle protytypes
JSAMPLE * read_JPEG_file (char * filename, int & width, int & height);
GLOBAL(void) write_JPEG_file (char * filename, int quality, JSAMPLE * image_buffer, int width, int height);
#define JPEGSAMPLE(i) (unsigned int)RGB[i]
#define RGB_BELOW(x) ( R <= x && G <= x && B <= x )


/*******************************************************************
 * Build the ntuple per image
 */

void BuildNtuple(JSAMPLE * RGB, int width_i, int height_i, input_parameters ip) {

  //cout << ip.rootfile << "," << ip.roottree << endl;
  
  // Variables
  int colorR;
  int colorG;
  int colorB;
  int pixx;
  int pixy;
  //double colorgor;
  //double colorbor;
  int width = width_i;
  int height = height_i;
  int drlevel = ip.drlevel;
  
  std::map<int,int> colors;
  
  int square = 100;
  string be;

  // Branches
  string temp;//,temp1;
  //cout << "Entering the square pixel" << endl;
  for(int i = 0 ; i< square*square; i++){
    colors[i] = 0;
    be = "pix";
    std::ostringstream oss;
    oss << i;
    be += oss.str();
    temp = be + "/I";
    //    sprintf(be,"pix%d",i);
    //temp1 = strcat(temp1, be);
    //temp = strcat(temp1 , "/I");
    //cout << "Pixel " << be << " being written as " << temp << endl;
    ip.roottree->Branch(be.c_str(), &colors[i], temp.c_str());
    oss.clear();
    be = "";
  }
  /*
  ip.roottree->Branch("Red", &colorR, "Red/I");
  ip.roottree->Branch("Green", &colorG, "Green/I");
  ip.roottree->Branch("Blue", &colorB, "Blue/I");
  //ip.roottree->Branch("GreenOverRed", &colorgor, "GreenOverRed/D");
  //ip.roottree->Branch("BlueOverRed", &colorbor, "BlueOverRed/D");
  ip.roottree->Branch("PixelX", &pixx, "PixelX/I");
  ip.roottree->Branch("PixelY", &pixy, "PixelY/I");
  ip.roottree->Branch("drlevel", &drlevel, "drlevel/I");
  //ip.roottree->Branch("width", &width, "width/I");
  //ip.roottree->Branch("height", &height, "height/I");
  */
  ip.roottree->Branch("drlevel", &drlevel, "drlevel/I");
  int R = 0, G = 0, B = 0;
  int k = 0;
  for (int i = 0 ; i < width ; i++) {
    for (int j = 0 ; j < height ; j++) {
      R = JPEGSAMPLE(3*(j*width + i)    );
      G = JPEGSAMPLE(3*(j*width + i) + 1);
      B = JPEGSAMPLE(3*(j*width + i) + 2);
      
      // Get rid of the black
      if ( RGB_BELOW( 3 ) ) continue;
      
      colors[k] = R + 256*G + 256*256*B;
      /*
      // Otherwise fill data
      colorR = R;
      colorG = G;
      colorB = B;
      
      pixx = i;
      pixy = j;
      */
      /*
      // ratios
      if ( R > 0 ) {
	colorbor = (double)B/(double)R;
	colorgor = (double)G/(double)R;
      } else {
	colorbor = 0.;
	colorgor = 0.;
      }
      */
      // Fill here one event
      //ip.roottree->Fill();
      k++;
    }
  }
  ip.roottree->Fill();
  
}


/*******************************************************************
 *  Make a selection in the image if necessary
 */
void InImageSelection(string fn, JSAMPLE * RGB, int width, int height) {

  

  ///// Alberto Color Mode selection
  std::map<int,int> colors;
  int key;
  int R = 0, B = 0, G = 0;
  for (int ii = 0 ; ii < width ; ii++) {
    for (int jj = 0 ; jj < height ; jj++) {
      R = JPEGSAMPLE(3*(jj*width + ii));
      G = JPEGSAMPLE(3*(jj*width + ii) + 1);
      B = JPEGSAMPLE(3*(jj*width + ii) + 2);
      if( RGB_BELOW(3) ){
	RGB[3*(jj*width + ii)] = 255;
	RGB[3*(jj*width + ii) + 1] = 255;
	RGB[3*(jj*width + ii) + 2] = 255;
      }
      /*
      key = R + G*256 + B*256*256;

      if( ! RGB_BELOW(3) ){
	if( colors.find(key) == colors.end() ){
	  colors[key] = 0; 
	}
	else{
	  colors[key]++;
	}
      }
      */
      //cout << "Color " <<  JPEGSAMPLE(jj*width + ii) << endl; 
    }
  }  

  /*

  std::map<int,int>::iterator it;
  int mode = 0;
  int keymode = 0;
  for(it = colors.begin() ; it != colors.end() ; it++){
    if(it->second > mode){
      mode = it->second;
      keymode = it->first;
      //cout << "key " << keymode << " Mode: " << mode << endl;
    }
  }

  for (int ii = 0 ; ii < width ; ii++) {
    for (int jj = 0 ; jj < height ; jj++) {
      R = JPEGSAMPLE(3*(jj*width + ii));
      G = JPEGSAMPLE(3*(jj*width + ii) + 1);
      B = JPEGSAMPLE(3*(jj*width + ii) + 2);
      key = R + G*256 + B*256*256;
      if( RGB_BELOW(3) ){
	RGB[3*(jj*width + ii)] = 255;
	RGB[3*(jj*width + ii) + 1] = 255;
	RGB[3*(jj*width + ii) + 2] = 255;
      }
      else if(colors[key] > mode*0.9 ){
	RGB[3*(jj*width + ii)] = 255;
	RGB[3*(jj*width + ii) + 1] = 255;
	RGB[3*(jj*width + ii) + 2] = 255;
      }
    }
  }  

  */

  /* //// John Croping
  int cropSpan = 50;
  int searchSpan = 3;
  // Find the center ofthe rectangle to crop
  int centerx = 0, centery = 0;
  double maxmean = 0., mean = 0.;
  int maxi = 0;
  int maxj = 0;
  int regionR = 0, regionG = 0, regionB = 0;
  
  for (int ii = searchSpan ; ii < width - searchSpan ; ii++) {
    for (int jj = searchSpan ; jj < height - searchSpan ; jj++) {
      // span over a few neightbors now
      regionR = 0;
      regionG = 0;
      regionB = 0;
      for (int i = ii - searchSpan ; i < ii+searchSpan ; i++) {
	for (int j = jj - searchSpan ; j < jj+searchSpan ; j++) {
	  regionR += JPEGSAMPLE(3*(jj*width + ii)    );
	  regionG += JPEGSAMPLE(3*(jj*width + ii) + 1);
	  regionB += JPEGSAMPLE(3*(jj*width + ii) + 2);
	}
      }
      mean = (10*regionR + 10*regionG + regionB) / 3.;
      if( mean > maxmean ) {
	maxmean = mean;
	maxi = ii;
	maxj = jj;
      }
    }
  }
  // The crop area is defined by these two coordinates
  int x0 = maxi - cropSpan;
  int y0 = maxj - cropSpan;
  int x1 = maxi + cropSpan;
  int y1 = maxj + cropSpan;
  if(debug) cout << "[INFO] Crop : " << x0 << "," << y0 << " --> " << x1 << "," << y1 << endl;
  // // Erase what is not in the selection
  for (int ii = 0 ; ii < width ; ii++) {
    for (int jj = 0 ; jj < height ; jj++) {
      // Erase what is not in the selection
      if( ! ( ii >= x0 && ii <= x1 && jj >= y0 && jj <= y1 ) ) {
	RGB[3*(jj*width + ii)]   = 0;
	RGB[3*(jj*width + ii)+1] = 0;
	RGB[3*(jj*width + ii)+2] = 0;
      }
      //	cout << (unsigned int)RGB[3*(jj*width + ii)] << "," << (unsigned int)RGB[3*(jj*width + ii)+1] << "," << (unsigned int)RGB[3*(jj*width + ii)+2] << " ";
    }
  }

  */
  // Write the obtained sub-image to compare visually
  string cropfn = fn;
  cropfn += ".crop.jpeg";
  write_JPEG_file ((char *)cropfn.c_str(), 100, RGB, width, height);
}

/**
 *  Main
 */

int main(int argc, char ** argv) {
  
  // Input parameters
  input_parameters inputp = InputParameters(argc, argv);
  string cero = inputp.output_dir + "/0/";
  string uno = inputp.output_dir + "/1/";
  string dos = inputp.output_dir + "/2/";
  string tres = inputp.output_dir + "/3/";
  string cuatro = inputp.output_dir + "/4/";

  // If the output directory doesn't exist then create it
  if( ! DirectoryExists(inputp.output_dir) ) {
    if(debug) cout << "[INFO] mkdir --> ip.output_dir" << endl;
    mkdir(inputp.output_dir.c_str(),  S_IRWXU | S_IRWXG | S_IROTH | S_IXOTH);
    mkdir(cero.c_str(),  S_IRWXU | S_IRWXG | S_IROTH | S_IXOTH);
    mkdir(uno.c_str(),  S_IRWXU | S_IRWXG | S_IROTH | S_IXOTH);
    mkdir(dos.c_str(),  S_IRWXU | S_IRWXG | S_IROTH | S_IXOTH);
    mkdir(tres.c_str(),  S_IRWXU | S_IRWXG | S_IROTH | S_IXOTH);
    mkdir(cuatro.c_str(),  S_IRWXU | S_IRWXG | S_IROTH | S_IXOTH);
  }
  
  // Number of threads to be used
  static const int num_threads = GetConcurrentThreads();
  if(debug) cout << "\t-   concurentThreads    = " << num_threads << endl;
  inputp.nthreads = num_threads;

  // Get info from the cvs file
  vector<pair<string, int> > csvinfo = readCSVFile(__trainLabels);
  //DumpCSVInfo(csvinfo);
  
  // Process images
  ProcessImages( csvinfo, inputp );
  
  if(debug) cout << "[INFO] done." << endl;
  
  return 0;
}

void ProcessImages(vector<pair<string, int> > csvinfo, input_parameters ip) {

  vector<pair<string, int> >::iterator i  = csvinfo.begin();
  vector<pair<string, int> >::iterator iE = csvinfo.end();
  
  int threadCntr = 0;
  thread t[ip.nthreads];
  int nToProcess = (int)csvinfo.size();
  map<int, TTree*> treeMap; // to be written and closed at the end
  map<int, TFile*> fileMap; // to be written and closed at the end
  
  int cntr = 0;
  for( ; i != iE ; i++) {
    // set the filename indication from csv info
    ip.fnbit = (*i).first;
    ip.drlevel = (*i).second;
    
    //char b[1];
    string be;
    if(ip.drlevel == 0) be = "/0/";
    else if(ip.drlevel == 1) be = "/1/";
    else if(ip.drlevel == 2) be = "/2/";
    else if(ip.drlevel == 3) be = "/3/";
    else if(ip.drlevel == 4) be = "/4/";

    // The ROOT file and tree needs to be prepared and closed here
    //  because the ROOT objects are not thread safe
    // Bring the info to the ntuple
    string ntuplefn;
    ntuplefn += ip.output_dir;
    ntuplefn += be;
    ntuplefn += "Image";
    ntuplefn += ip.fnbit;
    ntuplefn += ".root";
    if(debug) cout << "[INFO] Creating ROOT file : " << ntuplefn << endl;
    TFile * rootfil = new TFile( ntuplefn.c_str(), "recreate" );
    TTree * tree = new TTree("Images", "Images");
    // pass these pointers to the thread
    ip.rootfile = rootfil;
    ip.roottree = tree;
    
    // Launch the thread
    fileMap[threadCntr] = ip.rootfile; // keep track
    treeMap[threadCntr] = ip.roottree;
    
    t[threadCntr] = thread(ProcessOneImage, threadCntr, ip);
    threadCntr++;
    
    // Pick up the threads when the maximum thread occ has been reached
    //  or if the selectedSlice has reached maximum
    if(threadCntr == ip.nthreads || cntr == nToProcess) {
      // All launched.  Join them to the main thread.
      for (int i = 0; i < threadCntr; ++i) {
	// join() returns when the thread execution has completed.
	// This synchronizes the moment this function returns with the completion of all the
	//  operations in the thread: This blocks the execution of the thread that calls this
	//  function until the function called on construction returns (if it hasn't yet).
	t[i].join();
	// Close root files
	fileMap[i]->cd();
	treeMap[i]->Write();
	fileMap[i]->Close();
      }
      threadCntr = 0;
    }
    cntr++;
  }
}

void ProcessOneImage(int threadId, input_parameters ip) {
	// Lower resolution
	string lowresfn = LowerRes( ip.fnbit, __lowres_percentage, "Lowres_", ip);
	// Read the lowres image
	int width = 0;
	int height = 0;
	JSAMPLE * RGB = read_JPEG_file( (char *) lowresfn.c_str(), width, height);
	// Select part of the image
	InImageSelection(lowresfn, RGB, width, height);
	// ROOT stuff
	BuildNtuple(RGB, width, height, ip);
	// free memory
	delete [] RGB;
	return;
}

string LowerRes(string file, double lowrespercentage, string prefix, input_parameters ip) {
  // prepare input and output filenames
  string infn = ip.data_dir;
  infn += '/' + file;
  infn += ".jpeg";
  
  // the new filename
  string outfn = ip.output_dir;
  outfn += '/' + prefix;
  outfn += file;
  outfn += ".jpeg";
  
  // See if the lower resolution file doesn't exist already
  struct stat buffer;
  if ( stat(outfn.c_str(), &buffer) == 0 ) {
    if(debug) cout << "[INFO] file : " << outfn << " already exists. Skip convert step." << endl;
    return outfn;
  }
  
  // prepare command
  TString command = "/bin/bash -c \"convert  -resize ";
  command += TString::Format("%.0f%% ", lowrespercentage);
  command += infn;
  command += ' ';
  command += outfn;
  command += "\"";
  
  if(debug) cout << "[INFO] Convert --> " << outfn << endl;
  
  // launch command
  int st = system( command.Data() );
  if ( st != 0 ) {
    if(debug) cout << "[ERRO] something wrong with command --> " << command.Data() << endl;
  }
  return outfn;
}

input_parameters InputParameters(int argc, char ** argv){
  if(argc < 3) {
    cout << "use: " << argv[0] << "  data(string)  converted_data(string)" << endl;
    cout << "   data:           Directory (full path) where original data can be found." << endl;
    cout << "   converted_data: Directory (full path) where processed data will be written." << endl;
    exit(0);
  }
  input_parameters ip;
  ip.data_dir = argv[1];
  ip.output_dir = argv[2];
  return ip;
}

vector<pair<string, int> > readCSVFile(string fn) {

  ifstream file ( fn.c_str() );
  
  vector<pair<string, int> > csv_info;
  
  string value, fn_prefix;
  int dr_level;
  int s = 0;
  int skipTitles = 0;
  char searchC = ',';
  bool finish = false;
  
  while ( file.good() ) {
    if(s%2==0){searchC = ',';}else{searchC='\n';}
    getline ( file, value, searchC ); // read until next search character
    if ( file.eof() ) {
      getline ( file, value );
      finish = true;
    }
    if (s%2==0 ) {
      fn_prefix = string( value, 0, value.length() );
    } else {
      dr_level = atoi( string( value, 0, value.length() ).c_str() );
			// ready to make an entry in the map
      if( skipTitles > 0 ) {
	csv_info.push_back( make_pair( fn_prefix, dr_level ) );
      } else { skipTitles++; }
    }
    s++;
    if(finish) break;
  }
  
  if(debug) cout << "[INFO] Read " << csv_info.size() << " entries from : " << fn << endl;

  file.close();
  
  return csv_info;
}

void DumpCSVInfo(vector<pair<string, int> > v) {

	vector<pair<string, int> >::iterator i  = v.begin();
	vector<pair<string, int> >::iterator iE = v.end();


	int cntr = 0;
	bool dotsFlag = false;
	int vsize = (int)v.size();
	if(debug) cout << "-- Dump CSV vector ---- first and last " << __lenghtprintcsv << " elements ------" << endl;
	for( ; i != iE ; i++) {

		if(cntr < __lenghtprintcsv || cntr > vsize - __lenghtprintcsv)
		  if(debug) cout << (*i).first << "[" << (*i).second << "] ";

		if(cntr == __lenghtprintcsv) dotsFlag = true;
		if(cntr == __lenghtprintcsv && dotsFlag) if(debug) cout << " ... ";

		cntr++;

	}

	cout << endl;

}

bool DirectoryExists(string dir) {

	DIR * dp;
	struct dirent * dirp;
	if((dp = opendir(dir.c_str())) == NULL) {
	  if(debug) cout << "[INFO] Error trying to open directory : " << dir << endl;
		return false;
	}

	return true;
}

static const int GetConcurrentThreads() {
  if(debug) cout << "[INFO] Concurrency setup :" << endl;
  // Determine the number of concurrent threads
  if(debug) cout << "\t-   Detecting number of cores using std::thread::hardware_concurrency ... ";
  unsigned concurentThreadsSupported = thread::hardware_concurrency();
  // If it is not detectable thread::hardware_concurrency() will return 0
  if ( concurentThreadsSupported == 0 ) {
    if(debug) cout << "couldn't detect it.  Will try an OS specific call." << endl;
  } else {
    if(debug) cout << "Successful." << endl;
    return concurentThreadsSupported * 2;
  }
  
  if(debug) cout << "\t-   Trying sysconf( _SC_NPROCESSORS_ONLN ) [linux specific] ... ";
  int concurentThreadsSupported_linux = sysconf( _SC_NPROCESSORS_ONLN ); // this value can be negative if call fails
  
  if ( concurentThreadsSupported_linux <= 0 ) {
    concurentThreadsSupported_linux = 2;
    if(debug) cout << "couldn't detect it. Manually setting " << concurentThreadsSupported_linux * 2 << " threads as value by default." << endl;
  } else {
    if(debug) cout << "Successful." << endl;
    return concurentThreadsSupported_linux * 2;
  }
  return concurentThreadsSupported_linux * 2;
}

/*
 * Sample routine for JPEG decompression.  We assume that the source file name
 * is passed in.  We want to return 1 on success, 0 on error.
 */

struct my_error_mgr {
	struct jpeg_error_mgr pub;	/* "public" fields */
	jmp_buf setjmp_buffer;	/* for return to caller */
};

typedef struct my_error_mgr * my_error_ptr;

/*
 * Here's the routine that will replace the standard error_exit method:
 */

METHODDEF(void)
my_error_exit (j_common_ptr cinfo)
{
	/* cinfo->err really points to a my_error_mgr struct, so coerce pointer */
	my_error_ptr myerr = (my_error_ptr) cinfo->err;

	/* Always display the message. */
	/* We could postpone this until after returning, if we chose. */
	(*cinfo->err->output_message) (cinfo);

	/* Return control to the setjmp point */
	longjmp(myerr->setjmp_buffer, 1);
}

JSAMPLE * 
read_JPEG_file (char * filename, int & width, int & height) {
  /* This struct contains the JPEG decompression parameters and pointers to
   * working space (which is allocated as needed by the JPEG library).
   */
  //cout << "Height " << height << endl;
  struct jpeg_decompress_struct cinfo;
  /* We use our private extension JPEG error handler.
   * Note that this struct must live as long as the main JPEG parameter
   * struct, to avoid dangling-pointer problems.
   */
  struct my_error_mgr jerr;
  /* More stuff */
  FILE * infile;		/* source file */
  JSAMPARRAY buffer;		/* Output row buffer */
  int row_stride;		/* physical row width in output buffer */
  
  /* In this example we want to open the input file before doing anything else,
   * so that the setjmp() error recovery below can assume the file is open.
   * VERY IMPORTANT: use "b" option to fopen() if you are on a machine that
   * requires it in order to read binary files.
   */
  
  if ((infile = fopen(filename, "rb")) == NULL) {
    fprintf(stderr, "can't open %s\n", filename);
    return 0x0;
  }
  
  /* Step 1: allocate and initialize JPEG decompression object */

  /* We set up the normal JPEG error routines, then override error_exit. */
  cinfo.err = jpeg_std_error(&jerr.pub);
  jerr.pub.error_exit = my_error_exit;
  /* Establish the setjmp return context for my_error_exit to use. */
  if (setjmp(jerr.setjmp_buffer)) {
    /* If we get here, the JPEG code has signaled an error.
     * We need to clean up the JPEG object, close the input file, and return.
     */
    jpeg_destroy_decompress(&cinfo);
    fclose(infile);
    return 0x0;
  }
  /* Now we can initialize the JPEG decompression object. */
  jpeg_create_decompress(&cinfo);
  
  /* Step 2: specify data source (eg, a file) */
  
  jpeg_stdio_src(&cinfo, infile);
  
  /* Step 3: read file parameters with jpeg_read_header() */
  
  (void) jpeg_read_header(&cinfo, TRUE);
  /* We can ignore the return value from jpeg_read_header since
   *   (a) suspension is not possible with the stdio data source, and
   *   (b) we passed TRUE to reject a tables-only JPEG file as an error.
   * See libjpeg.txt for more info.
   */
  
  /* Step 4: set parameters for decompression */
  
  /* In this example, we don't need to change any of the defaults set by
   * jpeg_read_header(), so we do nothing here.
   */
  
  /* Step 5: Start decompressor */
  
  (void) jpeg_start_decompress(&cinfo);
  /* We can ignore the return value since suspension is not possible
   * with the stdio data source.
   */
  
  /* We may need to do some setup of our own at this point before reading
   * the data.  After jpeg_start_decompress() we have the correct scaled
   * output image dimensions available, as well as the output colormap
   * if we asked for color quantization.
   * In this example, we need to make an output work buffer of the right size.
   */
  /* JSAMPLEs per row in output buffer */
  row_stride = cinfo.output_width * cinfo.output_components;
  /* Make a one-row-high sample array that will go away when done with image */
  buffer = (*cinfo.mem->alloc_sarray) ((j_common_ptr) &cinfo, JPOOL_IMAGE, row_stride, 1);
  
  /* Step 6: while (scan lines remain to be read) */
  /*           jpeg_read_scanlines(...); */
  
  // Allocate memory
  JSAMPLE * RGB = new JSAMPLE[row_stride * cinfo.output_height]; // this is 3*width*height
  
  /* Here we use the library's state variable cinfo.output_scanline as the
   * loop counter, so that we don't have to keep track ourselves.
   */
  
  int gcntr = 0;
  while (cinfo.output_scanline < cinfo.output_height) {
    /* jpeg_read_scanlines expects an array of pointers to scanlines.
     * Here the array is only one element long, but you could ask for
     * more than one scanline at a time if that's more convenient.
     */
    (void) jpeg_read_scanlines(&cinfo, buffer, 1);
    /* Assume put_scanline_someplace wants a pointer and sample count. */
    //set_scanline(buffer[0], row_stride);
    
    // I'll put this stuff in an array R,G,B
    for ( int i = 0 ; i < row_stride ; i++) {
      RGB[gcntr] = buffer[0][i];
      if(debug) cout << (unsigned int)buffer[0][i] << "," << (unsigned int)buffer[0][i+1] << "," << (unsigned int)buffer[0][i+2] << "  " ;
      gcntr++;
    }
    
  }
  
  
  /* Step 7: Finish decompression */
  
  (void) jpeg_finish_decompress(&cinfo);
  /* We can ignore the return value since suspension is not possible
   * with the stdio data source.
   */
  
  /* Step 8: Release JPEG decompression object */
  
  /* This is an important step since it will release a good deal of memory. */
  jpeg_destroy_decompress(&cinfo);
  
  /* After finish_decompress, we can close the input file.
   * Here we postpone it until after no more JPEG errors are possible,
   * so as to simplify the setjmp error logic above.  (Actually, I don't
   * think that jpeg_destroy can do an error exit, but why assume anything...)
   */
  fclose(infile);
  
  /* At this point you may want to check to see whether any corrupt-data
   * warnings occurred (test whether jerr.pub.num_warnings is nonzero).
   */
  
  /* And we're done! */
  width = cinfo.image_width;
  height = cinfo.image_height;
  //cout << "Height " << height << endl;
  return RGB;
}

GLOBAL(void)
write_JPEG_file (char * filename, int quality, JSAMPLE * image_buffer, int image_width, int image_height)
{
	/* This struct contains the JPEG compression parameters and pointers to
	 * working space (which is allocated as needed by the JPEG library).
	 * It is possible to have several such structures, representing multiple
	 * compression/decompression processes, in existence at once.  We refer
	 * to any one struct (and its associated working data) as a "JPEG object".
	 */
	struct jpeg_compress_struct cinfo;
	/* This struct represents a JPEG error handler.  It is declared separately
	 * because applications often want to supply a specialized error handler
	 * (see the second half of this file for an example).  But here we just
	 * take the easy way out and use the standard error handler, which will
	 * print a message on stderr and call exit() if compression fails.
	 * Note that this struct must live as long as the main JPEG parameter
	 * struct, to avoid dangling-pointer problems.
	 */
	struct jpeg_error_mgr jerr;
	/* More stuff */
	FILE * outfile;		/* target file */
	JSAMPROW row_pointer[1];	/* pointer to JSAMPLE row[s] */
	int row_stride;		/* physical row width in image buffer */

	/* Step 1: allocate and initialize JPEG compression object */

	/* We have to set up the error handler first, in case the initialization
	 * step fails.  (Unlikely, but it could happen if you are out of memory.)
	 * This routine fills in the contents of struct jerr, and returns jerr's
	 * address which we place into the link field in cinfo.
	 */
	cinfo.err = jpeg_std_error(&jerr);
	/* Now we can initialize the JPEG compression object. */
	jpeg_create_compress(&cinfo);

	/* Step 2: specify data destination (eg, a file) */
	/* Note: steps 2 and 3 can be done in either order. */

	/* Here we use the library-supplied code to send compressed data to a
	 * stdio stream.  You can also write your own code to do something else.
	 * VERY IMPORTANT: use "b" option to fopen() if you are on a machine that
	 * requires it in order to write binary files.
	 */
	if ((outfile = fopen(filename, "wb")) == NULL) {
		fprintf(stderr, "can't open %s\n", filename);
		exit(1);
	}
	jpeg_stdio_dest(&cinfo, outfile);

	/* Step 3: set parameters for compression */

	/* First we supply a description of the input image.
	 * Four fields of the cinfo struct must be filled in:
	 */
	cinfo.image_width = image_width; 	/* image width and height, in pixels */
	cinfo.image_height = image_height;
	cinfo.input_components = 3;		/* # of color components per pixel */
	cinfo.in_color_space = JCS_RGB; 	/* colorspace of input image */
	/* Now use the library's routine to set default compression parameters.
	 * (You must set at least cinfo.in_color_space before calling this,
	 * since the defaults depend on the source color space.)
	 */
	jpeg_set_defaults(&cinfo);
	/* Now you can set any non-default parameters you wish to.
	 * Here we just illustrate the use of quality (quantization table) scaling:
	 */
	jpeg_set_quality(&cinfo, quality, TRUE /* limit to baseline-JPEG values */);

	/* Step 4: Start compressor */

	/* TRUE ensures that we will write a complete interchange-JPEG file.
	 * Pass TRUE unless you are very sure of what you're doing.
	 */
	jpeg_start_compress(&cinfo, TRUE);

	/* Step 5: while (scan lines remain to be written) */
	/*           jpeg_write_scanlines(...); */

	/* Here we use the library's state variable cinfo.next_scanline as the
	 * loop counter, so that we don't have to keep track ourselves.
	 * To keep things simple, we pass one scanline per call; you can pass
	 * more if you wish, though.
	 */
	row_stride = image_width * 3;	/* JSAMPLEs per row in image_buffer */

	while (cinfo.next_scanline < cinfo.image_height) {
		/* jpeg_write_scanlines expects an array of pointers to scanlines.
		 * Here the array is only one element long, but you could pass
		 * more than one scanline at a time if that's more convenient.
		 */
		row_pointer[0] = & image_buffer[cinfo.next_scanline * row_stride];
		(void) jpeg_write_scanlines(&cinfo, row_pointer, 1);
	}

	/* Step 6: Finish compression */

	jpeg_finish_compress(&cinfo);
	/* After finish_compress, we can close the output file. */
	fclose(outfile);

	/* Step 7: release JPEG compression object */

	/* This is an important step since it will release a good deal of memory. */
	jpeg_destroy_compress(&cinfo);

	/* And we're done! */
}
