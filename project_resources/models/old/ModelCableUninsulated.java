//auto-generated model template
//template generated by MEIM
//template v 1.0
//author Shadowmage45 (shadowage_catapults@hotmail.com)
 
package foo.bad.pkg.set.yours.here;
 
 
public class ModelCableUninsulated extends ModelBase
{
 
ModelRenderer northWire;
ModelRenderer southWire;
ModelRenderer eastWire;
ModelRenderer westWire;
ModelRenderer centerWire;
ModelRenderer northWireDiag;
ModelRenderer southWireDiag;
ModelRenderer westWireDiag;
ModelRenderer eastWireDiag;
public ModelCableUninsulated(){
  northWire = new ModelRenderer(this,"northWire");
  northWire.setTextureOffset(0,0);
  northWire.setTextureSize(64,64);
  northWire.setRotationPoint(0.0f, 0.0f, 0.0f);
  setPieceRotation(northWire,0.0f, 0.0f, 0.0f);
  northWire.addBox(-1.0f,7.0f,-8.0f,2,1,7);
  southWire = new ModelRenderer(this,"southWire");
  southWire.setTextureOffset(0,8);
  southWire.setTextureSize(64,64);
  southWire.setRotationPoint(0.0f, 0.0f, 0.0f);
  setPieceRotation(southWire,0.0f, 0.0f, 0.0f);
  southWire.addBox(-1.0f,7.0f,1.0f,2,1,7);
  eastWire = new ModelRenderer(this,"eastWire");
  eastWire.setTextureOffset(0,18);
  eastWire.setTextureSize(64,64);
  eastWire.setRotationPoint(0.0f, 0.0f, 0.0f);
  setPieceRotation(eastWire,0.0f, 0.0f, 0.0f);
  eastWire.addBox(1.0f,7.0f,-1.0f,7,1,2);
  westWire = new ModelRenderer(this,"westWire");
  westWire.setTextureOffset(0,22);
  westWire.setTextureSize(64,64);
  westWire.setRotationPoint(0.0f, 0.0f, 0.0f);
  setPieceRotation(westWire,0.0f, 0.0f, 0.0f);
  westWire.addBox(-8.0f,7.0f,-1.0f,7,1,2);
  centerWire = new ModelRenderer(this,"centerWire");
  centerWire.setTextureOffset(20,22);
  centerWire.setTextureSize(64,64);
  centerWire.setRotationPoint(0.0f, 0.0f, 0.0f);
  setPieceRotation(centerWire,0.0f, 0.0f, 0.0f);
  centerWire.addBox(-1.0f,7.0f,-1.0f,2,1,2);
  northWireDiag = new ModelRenderer(this,"northWireDiag");
  northWireDiag.setTextureOffset(20,0);
  northWireDiag.setTextureSize(64,64);
  northWireDiag.setRotationPoint(0.0f, 0.0f, 0.0f);
  setPieceRotation(northWireDiag,0.0f, 0.0f, 0.0f);
  northWireDiag.addBox(-1.0f,7.0f,-9.0f,2,1,1);
  southWireDiag = new ModelRenderer(this,"southWireDiag");
  southWireDiag.setTextureOffset(20,4);
  southWireDiag.setTextureSize(64,64);
  southWireDiag.setRotationPoint(0.0f, 0.0f, 0.0f);
  setPieceRotation(southWireDiag,0.0f, 0.0f, 0.0f);
  southWireDiag.addBox(-1.0f,7.0f,8.0f,2,1,1);
  westWireDiag = new ModelRenderer(this,"westWireDiag");
  westWireDiag.setTextureOffset(20,14);
  westWireDiag.setTextureSize(64,64);
  westWireDiag.setRotationPoint(0.0f, 0.0f, 0.0f);
  setPieceRotation(westWireDiag,0.0f, 0.0f, 0.0f);
  westWireDiag.addBox(-9.0f,7.0f,-1.0f,1,1,2);
  eastWireDiag = new ModelRenderer(this,"eastWireDiag");
  eastWireDiag.setTextureOffset(20,8);
  eastWireDiag.setTextureSize(64,64);
  eastWireDiag.setRotationPoint(0.0f, 0.0f, 0.0f);
  setPieceRotation(eastWireDiag,0.0f, 0.0f, 0.0f);
  eastWireDiag.addBox(8.0f,7.0f,-1.0f,1,1,2);
  }
 
@Override
public void render(Entity entity, float f1, float f2, float f3, float f4, float f5, float f6)
  {
  super.render(entity, f1, f2, f3, f4, f5, f6);
  setRotationAngles(f1, f2, f3, f4, f5, f6, entity);
  northWire.render(f6);
  southWire.render(f6);
  eastWire.render(f6);
  westWire.render(f6);
  centerWire.render(f6);
  northWireDiag.render(f6);
  southWireDiag.render(f6);
  westWireDiag.render(f6);
  eastWireDiag.render(f6);
  }
 
public void setPieceRotation(ModelRenderer model, float x, float y, float z)
  {
  model.rotateAngleX = x;
  model.rotateAngleY = y;
  model.rotateAngleZ = z;
  }
}