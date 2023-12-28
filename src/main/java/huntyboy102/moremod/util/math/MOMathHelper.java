
package huntyboy102.moremod.util.math;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Random;

public class MOMathHelper {
	public static final double PI2 = Math.PI * 2;
	static final int p[] = new int[512], permutation[] = { 151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194,
			233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23, 190, 6, 148, 247, 120, 234, 75, 0, 26,
			197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168,
			68, 175, 74, 165, 71, 134, 139, 48, 27, 166, 77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220,
			105, 92, 41, 55, 46, 245, 40, 244, 102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208,
			89, 18, 169, 200, 196, 135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250,
			124, 123, 5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42,
			223, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9, 129, 22, 39,
			253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228, 251, 34, 242, 193, 238,
			210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157,
			184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254, 138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243,
			141, 128, 195, 78, 66, 215, 61, 156, 180 };
	public static double degreesToRadians = Math.PI / 180.0;

	static {
		for (int i = 0; i < 256; i++) {
			p[256 + i] = p[i] = permutation[i];
		}
	}

	public static Vector3f randomSpherePoint(double x0, double y0, double z0, Vec3 radius, Random rand) {
		double u = rand.nextDouble();
		double v = rand.nextDouble();
		double theta = 2 * Math.PI * u;
		double phi = Math.acos(2 * v - 1);
		double x = x0 + (radius.x * Math.sin(phi) * Math.cos(theta));
		double y = y0 + (radius.y * Math.sin(phi) * Math.sin(theta));
		double z = z0 + (radius.z * Math.cos(phi));
		return new Vector3f((float) x, (float) y, (float) z);
	}

	public static Vector3f randomCirclePoint(float radius, Random rand) {
		double u = rand.nextDouble();
		double theta = 2 * Math.PI * u;
		double x = (radius * Math.sin(theta));
		double z = (radius * Math.cos(theta));
		return new Vector3f((float) x, 0, (float) z);
	}

	public static boolean getBoolean(int number, int pos) {

		return ((number >> pos) & 1) == 1;
	}

	public static double nextGaussian(Random random, double mean, double variance) {
		return mean + random.nextGaussian() * variance;
	}

	@OnlyIn(Dist.CLIENT)
	public static Vec3 mouseToWorldRay(int mouseX, int mouseY, int width, int height) {
		double aspectRatio = ((double) width / (double) height);
		double fov = (Minecraft.getInstance().options.fov().get()).doubleValue() / 2d + 11 * (Math.PI / 180);
		Entity renderViewEntity = Minecraft.getInstance().getCameraEntity();

		double a = -((double) mouseX / (double) width - 0.5) * 2;
		double b = -((double) mouseY / (double) height - 0.5) * 2;
		double tanf = Math.tan(fov);

		float pitch = renderViewEntity.getXRot();
		float yawn = renderViewEntity.getYRot();

		Matrix4f rot = new Matrix4f();
		rot.rotate(yawn * (float) (Math.PI / 180), new Vector3f(0, -1, 0));
		rot.rotate(pitch * (float) (Math.PI / 180), new Vector3f(1, 0, 0));

		// Apply multiplication using the mul method
		Vector4f forward = new Vector4f(0, 0, 1, 0);
		Vector4f up = new Vector4f(0, 1, 0, 0);
		Vector4f left = new Vector4f(1, 0, 0, 0);

		rot.transform(forward);
		rot.transform(up);
		rot.transform(left);

		return new Vec3(forward.x, forward.y, forward.z)
				.add(left.x * tanf * aspectRatio * a, left.y * tanf * aspectRatio * a, left.z * tanf * aspectRatio * a)
				.add(up.x * tanf * b, up.y * tanf * b, up.z * tanf * b).normalize();
	}

	public static int setBoolean(int number, int pos, boolean value) {
		if (value) {
			return number | (1 << pos);
		} else {
			return number & ~(1 << pos);
		}
	}

	// t - time
	// b - from value
	// c - to value
	// d - maxTime
	public static double easeIn(double t, double b, double c, double d) {
		return c * (t /= d) * t * t * t + b;
	}

	public static int toInt(short leftShort, short rightShort) {

		return (int) leftShort | ((int) rightShort << 16);
	}

	public static short[] getShorts(int number) {
		return new short[] { (short) (number & 0xFFFFFF), (short) (number >> 16) };
	}

	public static Vector2f Intersects(Vector2f a1, Vector2f a2, Vector2f b1, Vector2f b2) {
		Vector2f b = new Vector2f();
		Vector2f d = new Vector2f();
		Vector2f c = new Vector2f();

		a2.sub(a1, b);
		b2.sub(b1, d);

		float bDotDPerp = b.x * d.y - b.y * d.x;

		// if b dot d == 0, it means the lines are parallel so have infinite
		// intersection points
		if (bDotDPerp == 0) {
			return null;
		}

		b1.sub(a1, c);
		float t = (c.x * d.y - c.y * d.x) / bDotDPerp;
		if (t < 0 || t > 1) {
			return null;
		}

		float u = (c.x * b.y - c.y * b.x) / bDotDPerp;
		if (u < 0 || u > 1) {
			return null;
		}

		b.mul(t);
		return a1.add(b, new Vector2f());
	}

	public static float distance(Vector2f one, Vector2f two) {
		return Mth.sqrt((one.x - two.x) * (one.x - two.x) + (one.y - two.y) * (one.y - two.y));
	}

	public static double distance(int x, int y, int z, int x1, int y1, int z1) {
		return Math.sqrt((x - x1) * (x - x1) + (y - y1) * (y - y1) + (z - z1) * (z - z1));
	}

	public static int distanceSqured(int x, int y, int z, int x1, int y1, int z1) {
		return (x - x1) * (x - x1) + (y - y1) * (y - y1) + (z - z1) * (z - z1);
	}

	public static boolean animationInRange(int time, int begin, int length) {
		return time >= begin && time < begin + length;
	}

	public static float Lerp(float form, float to, float time) {
		float newTime = Mth.clamp(time, 0, 1);
		return (1 - newTime) * form + newTime * to;
	}

	public static double Lerp(double form, double to, double time) {
		double newTime = Mth.clamp(time, 0, 1);
		return (1 - newTime) * form + newTime * to;
	}

	// JAVA REFERENCE IMPLEMENTATION OF IMPROVED NOISE - COPYRIGHT 2002 KEN PERLIN.
	static public double noise(double x, double y, double z) {
		int X = (int) Math.floor(x) & 255, // FIND UNIT CUBE THAT
				Y = (int) Math.floor(y) & 255, // CONTAINS POINT.
				Z = (int) Math.floor(z) & 255;
		x -= Math.floor(x); // FIND RELATIVE X,Y,Z
		y -= Math.floor(y); // OF POINT IN CUBE.
		z -= Math.floor(z);
		double u = fade(x), // COMPUTE FADE CURVES
				v = fade(y), // FOR EACH OF X,Y,Z.
				w = fade(z);
		int A = p[X] + Y, AA = p[A] + Z, AB = p[A + 1] + Z, // HASH INATES OF
				B = p[X + 1] + Y, BA = p[B] + Z, BB = p[B + 1] + Z; // THE 8 CUBE CORNERS,

		return lerp(w, lerp(v, lerp(u, grad(p[AA], x, y, z), // AND ADD
				grad(p[BA], x - 1, y, z)), // BLENDED
				lerp(u, grad(p[AB], x, y - 1, z), // RESULTS
						grad(p[BB], x - 1, y - 1, z))), // FROM 8
				lerp(v, lerp(u, grad(p[AA + 1], x, y, z - 1), // CORNERS
						grad(p[BA + 1], x - 1, y, z - 1)), // OF CUBE
						lerp(u, grad(p[AB + 1], x, y - 1, z - 1), grad(p[BB + 1], x - 1, y - 1, z - 1))));
	}

	static double fade(double t) {
		return t * t * t * (t * (t * 6 - 15) + 10);
	}

	static double lerp(double t, double a, double b) {
		return a + t * (b - a);
	}

	static double grad(int hash, double x, double y, double z) {
		int h = hash & 15; // CONVERT LO 4 BITS OF HASH CODE
		double u = h < 8 ? x : y, // INTO 12 GRADIENT DIRECTIONS.
				v = h < 4 ? y : h == 12 || h == 14 ? x : z;
		return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
	}

	public static void shuffleArray(Random rnd, int[] ar) {
		for (int i = ar.length - 1; i > 0; i--) {
			int index = rnd.nextInt(i + 1);
			// Simple swap
			int a = ar[index];
			ar[index] = ar[i];
			ar[i] = a;
		}
	}

	public static void setScale(Vector3f vec, Matrix4f src, Matrix4f dest) {
		if (dest == null) {
			dest = new Matrix4f();
		}

		dest.m00(src.m00() * vec.x);
		dest.m01(src.m01() * vec.x);
		dest.m02(src.m02() * vec.x);
		dest.m03(src.m03() * vec.x);

		dest.m10(src.m10() * vec.y);
		dest.m11(src.m11() * vec.y);
		dest.m12(src.m12() * vec.y);
		dest.m13(src.m13() * vec.y);

		dest.m20(src.m20() * vec.z);
		dest.m21(src.m21() * vec.z);
		dest.m22(src.m22() * vec.z);
		dest.m23(src.m23() * vec.z);
	}

}
