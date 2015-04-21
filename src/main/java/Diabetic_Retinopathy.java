
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.rosuda.JRI.Rengine;

/**
 * DiabeticRetinopathy
 *
 * A template for processing each pixel of either GRAY8, GRAY16, GRAY32 or
 * COLOR_RGB images.
 *
 * @author The Fiji Team
 */
public class Diabetic_Retinopathy implements PlugIn { //PlugInFilter {

    protected ImagePlus image;

    // image property members
    private int width;
    private int height;

    // plugin parameters
    public double value;
    public String name;

    public List<ImageHelper> ImagesHelperList;

    public Diabetic_Retinopathy() {

        this.ImagesHelperList = new ArrayList<ImageHelper>();

    }

    public void loadCSVFile(String csvFile) {

        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        String imageFnTitle = "";
        String DRlevelTitle = "";

        try {

            br = new BufferedReader(new FileReader(csvFile));
            int lineCntr = 0;

            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] parsedLine = line.split(cvsSplitBy);

                // Extract the titles
                if (lineCntr == 0) {
                    imageFnTitle = parsedLine[0];
                    DRlevelTitle = parsedLine[1];
                } else {

                    //System.out.println(imageFnTitle + ": " + parsedLine[0]
                    //        + " ," + DRlevelTitle + ": " + parsedLine[1]);
                    // Create an image helper for this file
                    ImageHelper s = new ImageHelper(parsedLine[0], Integer.parseInt(parsedLine[1]));
                    ImagesHelperList.add(s);

                }
                lineCntr++;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.printf("Number of files referenced in file = %d\n", ImagesHelperList.size());

    }

    public double[] getIntegralAboveBackground(String fn, double backgSubsRolling, double kfactor) {

        // open
        ImagePlus image = IJ.openImage(fn);

        //int sizey = img_matrix[].length;
        //ip.rotate( 45 );
        //imp
        image.show();
        //IJ.run("Out [-]", ""); IJ.run("Out [-]", "");
        //ImagePlus imageOriginal = image.duplicate();
        //imageOriginal.show();
        //IJ.run("Out [-]", ""); IJ.run("Out [-]", "");
        //IJ.run("Hi Lo Indicator");
        String bgSubtraction = "rolling=" + backgSubsRolling + "  stack";
        IJ.run("Subtract Background...", bgSubtraction);
        //IJ.run("Median...", "radius=1");

        ImageProcessor ip = image.getProcessor();
        int[][] img_matrix = ip.getIntArray();
        int sizex = img_matrix.length;
        int sizey = img_matrix[0].length;
        int imageSize = sizex * sizey;

        // Before calculating the integrals I need to limit the measurement
        //  to a resonable radius.  Please find the radius of the image, i.e. the edge.
        // Calculate the average inside the selected radius
        double aR = 0, aG = 0, aB = 0;
        int pixelsConsidered = 0;
        for (int i = 0; i < sizex; i++) {
            for (int j = 0; j < sizey; j++) {

                int B = img_matrix[i][j] & 0xFF;
                int G = img_matrix[i][j] >> 8 & 0xFF;
                int R = img_matrix[i][j] >> 16 & 0xFF;
                //System.out.printf("R:%d,G:%d,B:%d\n", R, G, B);
                if (R != 0 && G != 0 && B != 0) {
                    aR += R;
                    aG += G;
                    aB += B;
                    pixelsConsidered++;
                }

            }
        }

        aR /= pixelsConsidered;
        aG /= pixelsConsidered;
        aB /= pixelsConsidered;

        // Integral over the average
        double inR = 0, inG = 0, inB = 0;
        for (int i = 0; i < sizex; i++) {
            for (int j = 0; j < sizey; j++) {
                int B = img_matrix[i][j] & 0xFF;
                int G = img_matrix[i][j] >> 8 & 0xFF;
                int R = img_matrix[i][j] >> 16 & 0xFF;

                if (R != 0 && G != 0 && B != 0) {

                    if (R > aR * kfactor) {
                        inR += R;
                    }
                    if (G > aG * kfactor) {
                        inG += G;
                    }
                    if (B > aB * kfactor) {
                        inB += B;
                    }

                }
            }
        }

        System.out.println("sizex : " + sizex + ", sizey = " + sizey);
        System.out.printf("Average  R,G,B = %.1f, %.1f, %.1f\n", aR, aG, aB);
        System.out.printf("Integral R,G,B = %.1f, %.1f, %.1f\n", inR, inG, inB);

        double[] inVector = {(inR + inG + inB) / 3, inG, inB};

        return inVector;
    }

    /**
     * @see ij.plugin.filter.PlugInFilter#setup(java.lang.String, ij.ImagePlus)
     */
    /*
     @Override
     public int setup(String arg, ImagePlus imp) {
     if (arg.equals("about")) {
     showAbout();
     return DONE;
     }

     image = imp;
     return DOES_8G | DOES_16 | DOES_32 | DOES_RGB;
     }
     */
    /**
     * @see ij.plugin.filter.PlugInFilter#run(ij.process.ImageProcessor)
     */
    @Override
    public void run(String arg) { // run(ImageProcessor ip) {

        System.out.println("Run ...\n");
        String datapath = "/home/idarraga/storage/Diabetic_Retinopathy_Detection/sample/";
        loadCSVFile("/home/idarraga/storage/Diabetic_Retinopathy_Detection/trainLabels.csv");

        int setSize = ImagesHelperList.size();

        // prepare R plot
        int nImagesProcess = 10;

        double[] levV = new double[nImagesProcess];
        double[] intRV = new double[nImagesProcess];

        for (int cntr = 0; cntr < nImagesProcess; cntr++) {

            ImageHelper oneImageHelper = ImagesHelperList.get(cntr);
            String fn = datapath + oneImageHelper.getFilenamePrefix() + ".jpeg";
            System.out.printf("-- %d -- ", cntr);
            System.out.printf("%s | DRlevel = %d\n", fn, oneImageHelper.getDRLevel());
            double[] inVector = getIntegralAboveBackground(fn, 100, 2.0);

            levV[cntr] = oneImageHelper.getDRLevel();
            intRV[cntr] = inVector[0];

        }

        // R
        String[] R_args = {"--no-save"};
        Rengine re = new Rengine(R_args, false, null);

        long levV3 = re.rniPutDoubleArray(levV);
        long intRV3 = re.rniPutDoubleArray(intRV);

        re.rniAssign("drlevel", levV3, 0);
        re.rniAssign("var", intRV3, 0);

        // Plot
        re.eval("plot(drlevel, var)");

        //showBackground("/home/idarraga/storage/Diabetic_Retinopathy_Detection/sample/10_left.jpeg");
        //showBackground("/home/idarraga/storage/Diabetic_Retinopathy_Detection/sample/13_left.jpeg");
        //showBackground("/home/idarraga/storage/Diabetic_Retinopathy_Detection/sample/17_left.jpeg");
        //showBackground("/home/idarraga/storage/Diabetic_Retinopathy_Detection/sample/15_left.jpeg");
        //showBackground("/home/idarraga/storage/Diabetic_Retinopathy_Detection/sample/16_left.jpeg");

        /*
         // get width and height
         width = ip.getWidth();
         height = ip.getHeight();

         if (showDialog()) {
         process(ip);
         image.updateAndDraw();
         }
         */
    }

    private boolean showDialog() {
        GenericDialog gd = new GenericDialog("Process pixels");

        // default value is 0.00, 2 digits right of the decimal point
        gd.addNumericField("value", 0.00, 2);
        gd.addStringField("name", "John");

        gd.showDialog();
        if (gd.wasCanceled()) {
            return false;
        }

        // get entered values
        value = gd.getNextNumber();
        name = gd.getNextString();

        return true;
    }

    /**
     * Process an image.
     *
     * Please provide this method even if {@link ij.plugin.filter.PlugInFilter}
     * does require it; the method
     * {@link ij.plugin.filter.PlugInFilter#run(ij.process.ImageProcessor)} can
     * only handle 2-dimensional data.
     *
     * If your plugin does not change the pixels in-place, make this method
     * return the results and change the
     * {@link #setup(java.lang.String, ij.ImagePlus)} method to return also the
     * <i>DOES_NOTHING</i> flag.
     *
     * @param image the image (possible multi-dimensional)
     */
    public void process(ImagePlus image) {
        // slice numbers start with 1 for historical reasons
        for (int i = 1; i <= image.getStackSize(); i++) {
            process(image.getStack().getProcessor(i));
        }
    }

    // Select processing method depending on image type
    public void process(ImageProcessor ip) {
        int type = image.getType();
        if (type == ImagePlus.GRAY8) {
            process((byte[]) ip.getPixels());
        } else if (type == ImagePlus.GRAY16) {
            process((short[]) ip.getPixels());
        } else if (type == ImagePlus.GRAY32) {
            process((float[]) ip.getPixels());
        } else if (type == ImagePlus.COLOR_RGB) {
            process((int[]) ip.getPixels());
        } else {
            throw new RuntimeException("not supported");
        }
    }

    // processing of GRAY8 images
    public void process(byte[] pixels) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // process each pixel of the line
                // example: add 'number' to each pixel
                pixels[x + y * width] += (byte) value;
            }
        }
    }

    // processing of GRAY16 images
    public void process(short[] pixels) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // process each pixel of the line
                // example: add 'number' to each pixel
                pixels[x + y * width] += (short) value;
            }
        }
    }

    // processing of GRAY32 images
    public void process(float[] pixels) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // process each pixel of the line
                // example: add 'number' to each pixel
                pixels[x + y * width] += (float) value;
            }
        }
    }

    // processing of COLOR_RGB images
    public void process(int[] pixels) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // process each pixel of the line
                // example: add 'number' to each pixel
                pixels[x + y * width] += (int) value;
            }
        }
    }

    public void showAbout() {
        IJ.showMessage("DiabeticRetinopathy",
                "a template for processing each pixel of an image"
        );
    }

    /**
     * Main method for debugging.
     *
     * For debugging, it is convenient to have a method that starts ImageJ,
     * loads an image and calls the plugin, e.g. after setting breakpoints.
     *
     * @param args unused
     */
    public static void main(String[] args) {

        // set the plugins.dir property to make the plugin appear in the Plugins menu
        Class<?> clazz = Diabetic_Retinopathy.class;
        String url = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class").toString();
        String pluginsDir = url.substring(5, url.length() - clazz.getName().length() - 6);
        System.setProperty("plugins.dir", pluginsDir);

        // start ImageJ
        new ImageJ();

        /*
         double rollingBall = Prefs.get("EI_rollingBall.double", 100);
        
         String[] filters =  { "None","Median", "Sigma", "Smooth","Anisotropic Diffusion"};
         double gammaVal= Prefs.get("EI_gammaVal.double", 0.7);
         int filterID=(int)Prefs.get("EI_filterID.int", 0);
         double percSat = Prefs.get("EI_percSat.double", 0.1);
        
         GenericDialog gd = new GenericDialog("Image Enhance");
         gd.addNumericField("BG subtraction radius (zero for no adjustment)", rollingBall, 0);
         gd.addChoice("Filter to reduce noise", filters, filters[filterID]);
         gd.addNumericField("Percentage saturation (100 for no adjustment)", percSat, 2);
         gd.addNumericField("Gamma Value (zero for no adjustment)", gammaVal, 2);
         gd.showDialog();

         if (gd.wasCanceled()) {
         return;
         }
        
         // Get the info from the dialogue
         rollingBall= gd.getNextNumber();
         filterID= gd.getNextChoiceIndex();
         percSat= gd.getNextNumber();
         gammaVal= gd.getNextNumber();
         */
        // Identifica el nevio optico y obtener el perfil.
        // Un nervio optico sano deberia tener un perfil suave.
        //ImagePlus image = IJ.openImage("/home/idarraga/storage/Diabetic_Retinopathy_Detection/sample/16_left.jpeg");
        //image.show();
        // run the plugin
        IJ.runPlugIn(clazz.getName(), "");

        //new ij.ImageJ();
        //new Diabetic_Retinopathy().run("");
    }

}
