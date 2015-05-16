#include "weights/TMVAClassification_TMlpANN.h"
#include <cmath>

double TMVAClassification_TMlpANN::Value(int index,double in0,double in1,double in2,double in3,double in4) {
   input0 = (in0 - 123.792)/36.8406;
   input1 = (in1 - 88.124)/30.6937;
   input2 = (in2 - 58.5247)/27.39;
   input3 = (in3 - 276.627)/111.769;
   input4 = (in4 - 183.72)/94.2369;
   switch(index) {
     case 0:
         return neuron0xa47ec60();
     default:
         return 0.;
   }
}

double TMVAClassification_TMlpANN::Value(int index, double* input) {
   input0 = (input[0] - 123.792)/36.8406;
   input1 = (input[1] - 88.124)/30.6937;
   input2 = (input[2] - 58.5247)/27.39;
   input3 = (input[3] - 276.627)/111.769;
   input4 = (input[4] - 183.72)/94.2369;
   switch(index) {
     case 0:
         return neuron0xa47ec60();
     default:
         return 0.;
   }
}

double TMVAClassification_TMlpANN::neuron0xa462d80() {
   return input0;
}

double TMVAClassification_TMlpANN::neuron0xa461830() {
   return input1;
}

double TMVAClassification_TMlpANN::neuron0xa461b70() {
   return input2;
}

double TMVAClassification_TMlpANN::neuron0xa461eb0() {
   return input3;
}

double TMVAClassification_TMlpANN::neuron0xa4621f0() {
   return input4;
}

double TMVAClassification_TMlpANN::input0xa462660() {
   double input = -1.8413;
   input += synapse0xa4610b0();
   input += synapse0xa461500();
   input += synapse0xa462910();
   input += synapse0xa462950();
   input += synapse0xa462990();
   return input;
}

double TMVAClassification_TMlpANN::neuron0xa462660() {
   double input = input0xa462660();
   return ((input < -709. ? 0. : (1/(1+exp(-input)))) * 1)+0;
}

double TMVAClassification_TMlpANN::input0xa4629d0() {
   double input = -5.72686;
   input += synapse0xa47c050();
   input += synapse0xa47c090();
   input += synapse0xa47c0d0();
   input += synapse0xa47c110();
   input += synapse0xa47c150();
   return input;
}

double TMVAClassification_TMlpANN::neuron0xa4629d0() {
   double input = input0xa4629d0();
   return ((input < -709. ? 0. : (1/(1+exp(-input)))) * 1)+0;
}

double TMVAClassification_TMlpANN::input0xa47c190() {
   double input = 8.22888;
   input += synapse0xa47c4d0();
   input += synapse0xa47c510();
   input += synapse0xa47c550();
   input += synapse0xa47c590();
   input += synapse0xa47c5d0();
   return input;
}

double TMVAClassification_TMlpANN::neuron0xa47c190() {
   double input = input0xa47c190();
   return ((input < -709. ? 0. : (1/(1+exp(-input)))) * 1)+0;
}

double TMVAClassification_TMlpANN::input0xa47c610() {
   double input = -0.571601;
   input += synapse0xa47c950();
   input += synapse0xa47c990();
   input += synapse0xa461540();
   input += synapse0xa462b60();
   input += synapse0xa462ba0();
   return input;
}

double TMVAClassification_TMlpANN::neuron0xa47c610() {
   double input = input0xa47c610();
   return ((input < -709. ? 0. : (1/(1+exp(-input)))) * 1)+0;
}

double TMVAClassification_TMlpANN::input0xa47cae0() {
   double input = -1.47948;
   input += synapse0xa47cd90();
   input += synapse0xa47cdd0();
   input += synapse0xa47ce10();
   input += synapse0xa47ce50();
   input += synapse0xa47ce90();
   return input;
}

double TMVAClassification_TMlpANN::neuron0xa47cae0() {
   double input = input0xa47cae0();
   return ((input < -709. ? 0. : (1/(1+exp(-input)))) * 1)+0;
}

double TMVAClassification_TMlpANN::input0xa47ced0() {
   double input = 2.90338;
   input += synapse0xa47d210();
   input += synapse0xa47d250();
   input += synapse0xa47d290();
   input += synapse0xa47d2d0();
   input += synapse0xa47d310();
   return input;
}

double TMVAClassification_TMlpANN::neuron0xa47ced0() {
   double input = input0xa47ced0();
   return ((input < -709. ? 0. : (1/(1+exp(-input)))) * 1)+0;
}

double TMVAClassification_TMlpANN::input0xa47d350() {
   double input = -3.49889;
   input += synapse0xa47d690();
   input += synapse0xa47d6d0();
   input += synapse0xa47d710();
   input += synapse0xa47c9d0();
   input += synapse0xa47ca10();
   input += synapse0xa47ca50();
   return input;
}

double TMVAClassification_TMlpANN::neuron0xa47d350() {
   double input = input0xa47d350();
   return ((input < -709. ? 0. : (1/(1+exp(-input)))) * 1)+0;
}

double TMVAClassification_TMlpANN::input0xa47d960() {
   double input = -0.483514;
   input += synapse0xa47dca0();
   input += synapse0xa47dce0();
   input += synapse0xa47dd20();
   input += synapse0xa47dd60();
   input += synapse0xa47dda0();
   input += synapse0xa47dde0();
   return input;
}

double TMVAClassification_TMlpANN::neuron0xa47d960() {
   double input = input0xa47d960();
   return ((input < -709. ? 0. : (1/(1+exp(-input)))) * 1)+0;
}

double TMVAClassification_TMlpANN::input0xa47de20() {
   double input = 1.42333;
   input += synapse0xa47e160();
   input += synapse0xa47e1a0();
   input += synapse0xa47e1e0();
   input += synapse0xa47e220();
   input += synapse0xa47e260();
   input += synapse0xa47e2a0();
   return input;
}

double TMVAClassification_TMlpANN::neuron0xa47de20() {
   double input = input0xa47de20();
   return ((input < -709. ? 0. : (1/(1+exp(-input)))) * 1)+0;
}

double TMVAClassification_TMlpANN::input0xa47e2e0() {
   double input = -0.147373;
   input += synapse0xa47e620();
   input += synapse0xa47e660();
   input += synapse0xa47e6a0();
   input += synapse0xa47e6e0();
   input += synapse0xa47e720();
   input += synapse0xa47e760();
   return input;
}

double TMVAClassification_TMlpANN::neuron0xa47e2e0() {
   double input = input0xa47e2e0();
   return ((input < -709. ? 0. : (1/(1+exp(-input)))) * 1)+0;
}

double TMVAClassification_TMlpANN::input0xa47e7a0() {
   double input = -1.01037;
   input += synapse0xa47eae0();
   input += synapse0xa47eb20();
   input += synapse0xa47eb60();
   input += synapse0xa47eba0();
   input += synapse0xa47ebe0();
   input += synapse0xa47ec20();
   return input;
}

double TMVAClassification_TMlpANN::neuron0xa47e7a0() {
   double input = input0xa47e7a0();
   return ((input < -709. ? 0. : (1/(1+exp(-input)))) * 1)+0;
}

double TMVAClassification_TMlpANN::input0xa47ec60() {
   double input = -0.798652;
   input += synapse0xa47efa0();
   input += synapse0xa47efe0();
   input += synapse0xa47f020();
   input += synapse0xa47f060();
   input += synapse0xa47f0a0();
   return input;
}

double TMVAClassification_TMlpANN::neuron0xa47ec60() {
   double input = input0xa47ec60();
   return (input * 1)+0;
}

double TMVAClassification_TMlpANN::synapse0xa4610b0() {
   return (neuron0xa462d80()*-0.764512);
}

double TMVAClassification_TMlpANN::synapse0xa461500() {
   return (neuron0xa461830()*-4.90753);
}

double TMVAClassification_TMlpANN::synapse0xa462910() {
   return (neuron0xa461b70()*2.56781);
}

double TMVAClassification_TMlpANN::synapse0xa462950() {
   return (neuron0xa461eb0()*1.44331);
}

double TMVAClassification_TMlpANN::synapse0xa462990() {
   return (neuron0xa4621f0()*1.30791);
}

double TMVAClassification_TMlpANN::synapse0xa47c050() {
   return (neuron0xa462d80()*-0.542087);
}

double TMVAClassification_TMlpANN::synapse0xa47c090() {
   return (neuron0xa461830()*1.89581);
}

double TMVAClassification_TMlpANN::synapse0xa47c0d0() {
   return (neuron0xa461b70()*-0.570966);
}

double TMVAClassification_TMlpANN::synapse0xa47c110() {
   return (neuron0xa461eb0()*-1.77389);
}

double TMVAClassification_TMlpANN::synapse0xa47c150() {
   return (neuron0xa4621f0()*-3.11212);
}

double TMVAClassification_TMlpANN::synapse0xa47c4d0() {
   return (neuron0xa462d80()*-0.694921);
}

double TMVAClassification_TMlpANN::synapse0xa47c510() {
   return (neuron0xa461830()*-0.043236);
}

double TMVAClassification_TMlpANN::synapse0xa47c550() {
   return (neuron0xa461b70()*-0.99041);
}

double TMVAClassification_TMlpANN::synapse0xa47c590() {
   return (neuron0xa461eb0()*3.47429);
}

double TMVAClassification_TMlpANN::synapse0xa47c5d0() {
   return (neuron0xa4621f0()*-5.0882);
}

double TMVAClassification_TMlpANN::synapse0xa47c950() {
   return (neuron0xa462d80()*4.16958);
}

double TMVAClassification_TMlpANN::synapse0xa47c990() {
   return (neuron0xa461830()*-7.32298);
}

double TMVAClassification_TMlpANN::synapse0xa461540() {
   return (neuron0xa461b70()*3.59856);
}

double TMVAClassification_TMlpANN::synapse0xa462b60() {
   return (neuron0xa461eb0()*1.83918);
}

double TMVAClassification_TMlpANN::synapse0xa462ba0() {
   return (neuron0xa4621f0()*-0.31246);
}

double TMVAClassification_TMlpANN::synapse0xa47cd90() {
   return (neuron0xa462d80()*3.6168);
}

double TMVAClassification_TMlpANN::synapse0xa47cdd0() {
   return (neuron0xa461830()*3.24916);
}

double TMVAClassification_TMlpANN::synapse0xa47ce10() {
   return (neuron0xa461b70()*-0.48854);
}

double TMVAClassification_TMlpANN::synapse0xa47ce50() {
   return (neuron0xa461eb0()*-3.25794);
}

double TMVAClassification_TMlpANN::synapse0xa47ce90() {
   return (neuron0xa4621f0()*0.505571);
}

double TMVAClassification_TMlpANN::synapse0xa47d210() {
   return (neuron0xa462d80()*-3.66732);
}

double TMVAClassification_TMlpANN::synapse0xa47d250() {
   return (neuron0xa461830()*4.61292);
}

double TMVAClassification_TMlpANN::synapse0xa47d290() {
   return (neuron0xa461b70()*-1.74093);
}

double TMVAClassification_TMlpANN::synapse0xa47d2d0() {
   return (neuron0xa461eb0()*3.4683);
}

double TMVAClassification_TMlpANN::synapse0xa47d310() {
   return (neuron0xa4621f0()*0.638577);
}

double TMVAClassification_TMlpANN::synapse0xa47d690() {
   return (neuron0xa462660()*-4.48899);
}

double TMVAClassification_TMlpANN::synapse0xa47d6d0() {
   return (neuron0xa4629d0()*-5.75545);
}

double TMVAClassification_TMlpANN::synapse0xa47d710() {
   return (neuron0xa47c190()*6.30723);
}

double TMVAClassification_TMlpANN::synapse0xa47c9d0() {
   return (neuron0xa47c610()*-9.45998);
}

double TMVAClassification_TMlpANN::synapse0xa47ca10() {
   return (neuron0xa47cae0()*-3.49444);
}

double TMVAClassification_TMlpANN::synapse0xa47ca50() {
   return (neuron0xa47ced0()*6.48244);
}

double TMVAClassification_TMlpANN::synapse0xa47dca0() {
   return (neuron0xa462660()*-0.332547);
}

double TMVAClassification_TMlpANN::synapse0xa47dce0() {
   return (neuron0xa4629d0()*-0.737714);
}

double TMVAClassification_TMlpANN::synapse0xa47dd20() {
   return (neuron0xa47c190()*-0.017014);
}

double TMVAClassification_TMlpANN::synapse0xa47dd60() {
   return (neuron0xa47c610()*-1.03417);
}

double TMVAClassification_TMlpANN::synapse0xa47dda0() {
   return (neuron0xa47cae0()*-1.21115);
}

double TMVAClassification_TMlpANN::synapse0xa47dde0() {
   return (neuron0xa47ced0()*-0.6346);
}

double TMVAClassification_TMlpANN::synapse0xa47e160() {
   return (neuron0xa462660()*-1.94548);
}

double TMVAClassification_TMlpANN::synapse0xa47e1a0() {
   return (neuron0xa4629d0()*-0.736077);
}

double TMVAClassification_TMlpANN::synapse0xa47e1e0() {
   return (neuron0xa47c190()*1.52732);
}

double TMVAClassification_TMlpANN::synapse0xa47e220() {
   return (neuron0xa47c610()*-3.52298);
}

double TMVAClassification_TMlpANN::synapse0xa47e260() {
   return (neuron0xa47cae0()*-0.160787);
}

double TMVAClassification_TMlpANN::synapse0xa47e2a0() {
   return (neuron0xa47ced0()*2.45791);
}

double TMVAClassification_TMlpANN::synapse0xa47e620() {
   return (neuron0xa462660()*0.654663);
}

double TMVAClassification_TMlpANN::synapse0xa47e660() {
   return (neuron0xa4629d0()*0.723104);
}

double TMVAClassification_TMlpANN::synapse0xa47e6a0() {
   return (neuron0xa47c190()*-0.774799);
}

double TMVAClassification_TMlpANN::synapse0xa47e6e0() {
   return (neuron0xa47c610()*2.00599);
}

double TMVAClassification_TMlpANN::synapse0xa47e720() {
   return (neuron0xa47cae0()*0.784519);
}

double TMVAClassification_TMlpANN::synapse0xa47e760() {
   return (neuron0xa47ced0()*0.0833259);
}

double TMVAClassification_TMlpANN::synapse0xa47eae0() {
   return (neuron0xa462660()*-1.03655);
}

double TMVAClassification_TMlpANN::synapse0xa47eb20() {
   return (neuron0xa4629d0()*-0.281108);
}

double TMVAClassification_TMlpANN::synapse0xa47eb60() {
   return (neuron0xa47c190()*0.121569);
}

double TMVAClassification_TMlpANN::synapse0xa47eba0() {
   return (neuron0xa47c610()*-1.42313);
}

double TMVAClassification_TMlpANN::synapse0xa47ebe0() {
   return (neuron0xa47cae0()*-1.10225);
}

double TMVAClassification_TMlpANN::synapse0xa47ec20() {
   return (neuron0xa47ced0()*1.82763);
}

double TMVAClassification_TMlpANN::synapse0xa47efa0() {
   return (neuron0xa47d350()*1.42526);
}

double TMVAClassification_TMlpANN::synapse0xa47efe0() {
   return (neuron0xa47d960()*1.33906);
}

double TMVAClassification_TMlpANN::synapse0xa47f020() {
   return (neuron0xa47de20()*0.141523);
}

double TMVAClassification_TMlpANN::synapse0xa47f060() {
   return (neuron0xa47e2e0()*0.866202);
}

double TMVAClassification_TMlpANN::synapse0xa47f0a0() {
   return (neuron0xa47e7a0()*-0.763246);
}

