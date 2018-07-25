/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.ar.sceneform.samples.augmentedimage;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.android.filament.Texture;
import com.google.android.filament.View;
import com.google.ar.core.AugmentedImage;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.Material;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.rendering.ViewRenderable;

import java.util.concurrent.CompletableFuture;

/**
 * Node for rendering an augmented image. The image is framed by placing the virtual picture frame
 * at the corners of the augmented image trackable.
 */
@SuppressWarnings({"AndroidApiChecker"})
public class AugmentedImageNode extends AnchorNode {

  private static final String TAG = "AugmentedImageNode";

  // The augmented image represented by this node.
  private AugmentedImage image;

  // Models of the 4 corners.  We use completable futures here to simplify
  // the error handling and asynchronous loading.  The loading is started with the
  // first construction of an instance, and then used when the image is set.
  private static CompletableFuture<ModelRenderable> ulCorner;
  private static CompletableFuture<ModelRenderable> urCorner;
  private static CompletableFuture<ModelRenderable> lrCorner;
  private static CompletableFuture<ModelRenderable> llCorner;
  private static CompletableFuture<ModelRenderable> earthSphereRenderable;
  private ModelRenderable fifaSphereRenderable;
  private ModelRenderable ticketRenderable;
  private ModelRenderable furnitureCubeRenderable;
  private ViewRenderable bannerRenderable;

  private CompletableFuture<ViewRenderable> bannerControlsStage;

  private Context contextMain;


  private String frameLink1="https://www.pictureframes.com/";
  private String frameLink2="https://www.pepperfry.com/home-decor-photo-frames.html";
  private String frameLink3="https://www.walmart.com/browse/craig-frames/";
  private String frameLink4="https://www.ebay.com/str/craigframes";
  private String doorLink1="http://siongdoor.com/";

  private String fifaLink1="https://www.fifa.com";
  private String fifaLink2="https://www.fifa.com/worldcup/organisation/ticketing/index.html";

  private  void  makeFIFABall(Context context)
  {
    Vector3 pos = new Vector3(0,0,0);

    com.google.ar.sceneform.rendering.Texture.Builder builder= com.google.ar.sceneform.rendering.Texture.builder();
    builder.setSource(context,R.drawable.fifaball);
    builder.build().thenAccept(texture ->
            MaterialFactory.makeOpaqueWithTexture(context, texture).
                    thenAccept(material -> {
                      fifaSphereRenderable =
                              ShapeFactory.makeSphere(0.06f, new Vector3(0.0f, 0.0f, 0.0f), material);
                     // Toast.makeText(context,"Here",Toast.LENGTH_SHORT).show();
                    })
    );



    ViewRenderable.builder()
            .setView(context, R.layout.fifabanner)
            .build()
            .thenAccept(renderable ->{
              bannerRenderable = renderable;
              bannerRenderable.setPixelsToMetersRatio(500);
           //   Toast.makeText(context,Float.toString(bannerRenderable.getPixelsToMetersRatio()),Toast.LENGTH_SHORT).show();
            });
  }

  private void makeTicketcube(Context context)
  {
    Vector3 pos = new Vector3(0,0,0);

    com.google.ar.sceneform.rendering.Texture.Builder builder= com.google.ar.sceneform.rendering.Texture.builder();
    builder.setSource(context,R.drawable.tickets);
    builder.build().thenAccept(texture ->
            MaterialFactory.makeOpaqueWithTexture(context, texture).
                    thenAccept(material -> {
                      ticketRenderable =
                              ShapeFactory.makeCube( new Vector3(0.15f, 0.001f, 0.075f),
                                      new Vector3(0.0f, 0.0f, 0.0f), material);
                      // Toast.makeText(context,"Here",Toast.LENGTH_SHORT).show();
                    })
    );
  }

  private void makeFurnitureCube(Context context)
  {
    Vector3 pos = new Vector3(0,0,0);

    com.google.ar.sceneform.rendering.Texture.Builder builder= com.google.ar.sceneform.rendering.Texture.builder();
    builder.setSource(context,R.drawable.door);
    builder.build().thenAccept(texture ->
            MaterialFactory.makeOpaqueWithTexture(context, texture).
                    thenAccept(material -> {
                      furnitureCubeRenderable =
                              ShapeFactory.makeCube( new Vector3(0.1f, 0.2f, 0.001f),
                                      new Vector3(0.0f, 0.0f, 0.0f), material);
                      // Toast.makeText(context,"Here",Toast.LENGTH_SHORT).show();
                    })
    );
  }

  public AugmentedImageNode(Context context) {
    // Upon construction, start loading the models for the corners of the frame.
    if (ulCorner == null) {
      ulCorner =
          ModelRenderable.builder()
              .setSource(context, Uri.parse("models/frame_upper_left.sfb"))
              .build();
      urCorner =
          ModelRenderable.builder()
              .setSource(context, Uri.parse("models/frame_upper_right.sfb"))
              .build();
      llCorner =
          ModelRenderable.builder()
              .setSource(context, Uri.parse("models/frame_lower_left.sfb"))
              .build();
      lrCorner =
          ModelRenderable.builder()
              .setSource(context, Uri.parse("models/frame_lower_right.sfb"))
              .build();

      bannerControlsStage=ViewRenderable.builder().setView(context, R.layout.fifabanner).build();

/*      earthSphereRenderable=ModelRenderable.builder()
              .setSource(context, Uri.parse("models/Earth.sfb"))
              .build();
              */
      makeFIFABall(context);
      makeTicketcube(context);
      makeFurnitureCube(context);
      contextMain=context;

    }
  }

  /**
   * Called when the AugmentedImage is detected and should be rendered. A Sceneform node tree is
   * created based on an Anchor created from the image. The corners are then positioned based on the
   * extents of the image. There is no need to worry about world coordinates since everything is
   * relative to the center of the image, which is the parent node of the corners.
   */

  void setLinkToNode( Node node, String link, String name)
  {
    node.setOnTapListener(new Node.OnTapListener(){
      @Override
      public void onTap(HitTestResult hitTestResult, MotionEvent motionEvent) {
        //    Toast.makeText(contextMain, "You hit me!",Toast.LENGTH_SHORT).show();
        Toast.makeText(contextMain, name+" CLICKED",Toast.LENGTH_LONG).show();
        Uri uri = Uri.parse(link); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        contextMain.startActivity(intent);
      }
    });
  }

  void makeWindowAd(AugmentedImage image)
  {
    // Make the 4 corner nodes.
    Vector3 localPosition = new Vector3();
    Node cornerNode;

    // Upper left corner.
    localPosition.set(-0.5f * image.getExtentX(), 0.0f, -0.5f * image.getExtentZ());
    cornerNode = new Node();
    cornerNode.setParent(this);
    cornerNode.setLocalPosition(localPosition);
    cornerNode.setRenderable(ulCorner.getNow(null));
    setLinkToNode(cornerNode,frameLink1,"Upper Left WINDOW FRAME");


    // Upper right corner.
    localPosition.set(0.5f * image.getExtentX(), 0.0f, -0.5f * image.getExtentZ());
    cornerNode = new Node();
    cornerNode.setParent(this);
    cornerNode.setLocalPosition(localPosition);
    cornerNode.setRenderable(urCorner.getNow(null));
    setLinkToNode(cornerNode,frameLink2,"Upper Right WINDOW FRAME");

    // Lower right corner.
    localPosition.set(0.5f * image.getExtentX(), 0.0f, 0.5f * image.getExtentZ());
    cornerNode = new Node();
    cornerNode.setParent(this);
    cornerNode.setLocalPosition(localPosition);
    cornerNode.setRenderable(lrCorner.getNow(null));
    setLinkToNode(cornerNode,frameLink3,"Lower Right WINDOW FRAME");

    // Lower left corner.
    localPosition.set(-0.5f * image.getExtentX(), 0.0f, 0.5f * image.getExtentZ());
    cornerNode = new Node();
    cornerNode.setParent(this);
    cornerNode.setLocalPosition(localPosition);
    cornerNode.setRenderable(llCorner.getNow(null));
    setLinkToNode(cornerNode,frameLink4,"Lower Left WINDOW FRAME");

    //Center
    BallNode ballNode = new BallNode();
    ballNode.setParent(this);
    ballNode.setLocalPosition(new Vector3(0.0f, 0.1f, 0.0f));
    ballNode.setRenderable(furnitureCubeRenderable);
    setLinkToNode(ballNode, doorLink1,"DOOR");

  }

  void makeFIFAAd(AugmentedImage image)
  {
    //Centre
    BallNode ballNode = new BallNode();
    ballNode.setParent(this);
    ballNode.setLocalPosition(new Vector3(0.0f, 0.1f, 0.0f));
    ballNode.setRenderable(fifaSphereRenderable);
    setLinkToNode(ballNode,fifaLink1,"FIFA BALL");


    //Centre
    BallNode ticketNode = new BallNode();
    ticketNode.setParent(this);
    ticketNode.setLocalPosition(new Vector3(0.0f, 0.0f, 0.0f));
    ticketNode.setRenderable(ticketRenderable);
    setLinkToNode(ticketNode,fifaLink2,"FIFA TICKET");

/*
    Node bannerNode = new Node();
    bannerNode.setParent(ballNode);
    bannerNode.setLocalPosition(new Vector3(0.0f, 0.1f, 0.0f));
    bannerNode.setRenderable(bannerControlsStage.getNow(null));
    */
  }

  @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
  public void setImage(AugmentedImage image) {
    this.image = image;

    // If any of the models are not loaded, then recurse when all are loaded.
    if (!ulCorner.isDone() || !urCorner.isDone() || !llCorner.isDone() || !lrCorner.isDone()) {
      CompletableFuture.allOf(ulCorner, urCorner, llCorner, lrCorner)
          .thenAccept((Void aVoid) -> setImage(image))
          .exceptionally(
              throwable -> {
                Log.e(TAG, "Exception loading", throwable);
                return null;
              });
    }

    // Set the anchor based on the center of the image.
    setAnchor(image.createAnchor(image.getCenterPose()));


    String currAd = AugmentedAdBrokerService.getAd(contextMain);
    if(currAd.equals("furniture"))
      makeWindowAd(image);
    else if(currAd.equals("fifa"))
      makeFIFAAd(image);







  }

  public AugmentedImage getImage() {
    return image;
  }
}
