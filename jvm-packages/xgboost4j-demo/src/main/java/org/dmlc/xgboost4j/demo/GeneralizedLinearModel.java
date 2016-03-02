/*
 Copyright (c) 2014 by Contributors

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package org.dmlc.xgboost4j.demo;

import org.dmlc.xgboost4j.*;
import org.dmlc.xgboost4j.demo.util.CustomEval;

import java.util.HashMap;

/**
 * this is an example of fit generalized linear model in xgboost
 * basically, we are using linear model, instead of tree for our boosters
 *
 * @author hzx
 */
public class GeneralizedLinearModel {
  public static void main(String[] args) throws XGBoostError {
    // load file from text file, also binary buffer generated by xgboost4j
    DMatrix trainMat = new DMatrix("../../demo/data/agaricus.txt.train");
    DMatrix testMat = new DMatrix("../../demo/data/agaricus.txt.test");

    //specify parameters
    //change booster to gblinear, so that we are fitting a linear model
    // alpha is the L1 regularizer
    //lambda is the L2 regularizer
    //you can also set lambda_bias which is L2 regularizer on the bias term
    HashMap<String, Object> params = new HashMap<String, Object>();
    params.put("alpha", 0.0001);
    params.put("silent", 1);
    params.put("objective", "binary:logistic");
    params.put("booster", "gblinear");

    //normally, you do not need to set eta (step_size)
    //XGBoost uses a parallel coordinate descent algorithm (shotgun),
    //there could be affection on convergence with parallelization on certain cases
    //setting eta to be smaller value, e.g 0.5 can make the optimization more stable
    //param.put("eta", "0.5");


    //specify watchList
    HashMap<String, DMatrix> watches = new HashMap<String, DMatrix>();
    watches.put("train", trainMat);
    watches.put("test", testMat);

    //train a booster
    int round = 4;
    Booster booster = XGBoost.train(params, trainMat, round, watches, null, null);

    float[][] predicts = booster.predict(testMat);

    CustomEval eval = new CustomEval();
    System.out.println("error=" + eval.eval(predicts, testMat));
  }
}
