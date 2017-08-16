package alec_wam.CrystalMod.client.model;

import java.util.Map;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableMap;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.common.model.TRSRTransformation;

public abstract class Fixed3DBlockModel implements IPerspectiveAwareModel {

	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
		return IPerspectiveAwareModel.MapWrapper.handlePerspective(this, getBlockTransforms(), cameraTransformType);
	}
	/** Get the default transformations for inside inventories and third person */
    protected static ImmutableMap<TransformType, TRSRTransformation> getBlockTransforms() {
        ImmutableMap.Builder<TransformType, TRSRTransformation> builder = ImmutableMap.builder();

        // Copied from ForgeBlockStateV1
        /*builder.put(TransformType.THIRD_PERSON, TRSRTransformation.blockCenterToCorner(new TRSRTransformation(new Vector3f(0, 1.5f / 16, -2.75f / 16),
                TRSRTransformation.quatFromYXZDegrees(new Vector3f(10, -45, 170)), new Vector3f(0.375f, 0.375f, 0.375f), null)));
*/
        // Gui
        {
            Matrix4f rotationMatrix = new Matrix4f();
            rotationMatrix.setIdentity();
            rotationMatrix = rotateTowardsFace(EnumFacing.SOUTH, EnumFacing.EAST);

            Matrix4f result = new Matrix4f();
            result.setIdentity();
            // Multiply by the last matrix transformation FIRST
            result.mul(rotationMatrix);

            TRSRTransformation trsr = new TRSRTransformation(result);

            builder.put(TransformType.GUI, trsr);
        }

        return builder.build();
    }
	
	/** Rotates towards the given face, assuming what you want to rotate from is WEST. */
    public static Matrix4f rotateTowardsFace(EnumFacing face) {
        return new Matrix4f(rotationMap.get(face));
    }

    /** Rotates towards the given face, from the specified face */
    public static Matrix4f rotateTowardsFace(EnumFacing from, EnumFacing to) {
        Matrix4f fromMatrix = new Matrix4f(rotateTowardsFace(from));
        // Because we want to do the opposite of what this does
        fromMatrix.invert();

        Matrix4f toMatrix = rotateTowardsFace(to);
        Matrix4f result = new Matrix4f(toMatrix);
        result.mul(fromMatrix);
        return result;
    }
    
    private static final Map<EnumFacing, Matrix4f> rotationMap;

    static {
        ImmutableMap.Builder<EnumFacing, Matrix4f> builder = ImmutableMap.builder();
        for (EnumFacing face : EnumFacing.values()) {
            Matrix4f mat = new Matrix4f();
            mat.setIdentity();

            if (face == EnumFacing.WEST) {
                builder.put(face, mat);
                continue;
            }
            mat.setTranslation(new Vector3f(0.5f, 0.5f, 0.5f));
            Matrix4f m2 = new Matrix4f();
            m2.setIdentity();

            if (face.getAxis() == Axis.Y) {
                AxisAngle4f axisAngle = new AxisAngle4f(0, 0, 1, (float) Math.PI * 0.5f * -face.getFrontOffsetY());
                m2.setRotation(axisAngle);
                mat.mul(m2);

                m2.setIdentity();
                m2.setRotation(new AxisAngle4f(1, 0, 0, (float) Math.PI * (1 + face.getFrontOffsetY() * 0.5f)));
                mat.mul(m2);
            } else {
                int ang;
                if (face == EnumFacing.EAST) ang = 2;
                else if (face == EnumFacing.NORTH) ang = 3;
                else ang = 1;
                AxisAngle4f axisAngle = new AxisAngle4f(0, 1, 0, (float) Math.PI * 0.5f * ang);
                m2.setRotation(axisAngle);
                mat.mul(m2);
            }

            m2.setIdentity();
            m2.setTranslation(new Vector3f(-0.5f, -0.5f, -0.5f));
            mat.mul(m2);
            builder.put(face, mat);
        }
        rotationMap = builder.build();
    }
	
}
