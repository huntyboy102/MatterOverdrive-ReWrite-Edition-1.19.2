
package huntyboy102.moremod.client.sound;

import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.client.FMLClientHandler;

public class GravitationalAnomalySound extends PositionedSound implements ITickableSound {

	boolean donePlaying = false;
	double range;

	public GravitationalAnomalySound(SoundEvent sound, SoundCategory category, BlockPos pos, float volume,
			double range) {
		super(sound, category);
		setPosition(pos.getX(), pos.getY(), pos.getZ());
		this.volume = volume;
		this.range = range;
		this.attenuationType = AttenuationType.NONE;
		this.repeat = true;
	}

	@Override
	public boolean isDonePlaying() {
		return donePlaying;
	}

	public void stopPlaying() {
		donePlaying = true;
	}

	public void startPlaying() {
		donePlaying = false;
	}

	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}

	public void setVolume(float volume) {
		this.volume = volume;
	}

	public void setRange(double range) {
		this.range = range;
	}

	public void setPosition(float x, float y, float z) {
		this.xPosF = x;
		this.yPosF = y;
		this.zPosF = z;
	}

	@Override
	public void update() {
		EntityPlayerSP mp = FMLClientHandler.instance().getClient().player;
		double distance = new Vec3d(xPosF, yPosF, zPosF).distanceTo(mp.getPositionVector());
		volume = 1 - (float) (distance / range);
	}
}
