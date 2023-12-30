
package huntyboy102.moremod.util;

import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import huntyboy102.moremod.api.inventory.UpgradeTypes;
import huntyboy102.moremod.api.starmap.PlanetStatType;
import huntyboy102.moremod.api.weapon.IWeaponStat;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.ChatFormatting;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.text.DecimalFormat;

public class MOStringHelper {
	public static final String MORE_INFO = ChatFormatting.RESET.toString() + ChatFormatting.GRAY + "Hold "
			+ ChatFormatting.ITALIC + ChatFormatting.YELLOW + "Shift" + ChatFormatting.RESET.toString()
			+ ChatFormatting.GRAY + " for Details.";

	public static String formatNumber(double number) {
		return formatNumber(number, "0.00");
	}

	public static String formatNumber(double number, String decialFormat) {
		if (number > 1000000000000000D) {
			return new DecimalFormat(decialFormat + "Q").format((number / 1000000000000000.00D));
		}
		if (number > 1000000000000D) {
			return new DecimalFormat(decialFormat + "T").format((number / 1000000000000.00D));
		} else if (number > 1000000000D) {
			return new DecimalFormat(decialFormat + "B").format((number / 1000000000.00D));
		} else if (number > 1000000D) {
			return new DecimalFormat(decialFormat + "M").format((number / 1000000.00D));
		} else if (number > 1000D) {
			return new DecimalFormat(decialFormat + "K").format((number / 1000.00D));
		} else {
			return new DecimalFormat(decialFormat).format(number);
		}
	}

	public static String formatRemainingTime(float seccounds) {
		return formatRemainingTime(seccounds, false);
	}

	public static String formatRemainingTime(float seccounds, boolean shotSufix) {
		if (seccounds > 3600) {
			return String.format("%s%s", String.valueOf(Math.round(seccounds / 3600)), shotSufix ? "h" : " hr");
		} else if (seccounds > 60 && seccounds < 60 * 60) {
			return String.format("%s%s", String.valueOf(Math.round(seccounds / 60)), shotSufix ? "m" : " min");
		} else {
			return String.format("%s%s", String.valueOf(Math.round(seccounds)), shotSufix ? "s" : " sec");
		}
	}

	public static String typingAnimation(String message, int time, int maxTime) {
		float percent = ((float) time / (float) maxTime);
		int messageCount = message.length();
		return message.substring(0, Mth.clamp(Math.round(messageCount * percent), 0, messageCount));
	}

	public static boolean hasTranslation(String key) {
		return MatterOverdriveRewriteEdition.PROXY.hasTranslation(key);
	}

	public static String translateToLocal(String key, Object... params) {
		return MatterOverdriveRewriteEdition.PROXY.translateToLocal(key, params);
	}

	public static String translateToLocal(PlanetStatType statType) {
		return translateToLocal("planet_stat." + statType.getUnlocalizedName() + ".name");
	}

	public static String translateToLocal(UpgradeTypes type) {
		return translateToLocal("upgradetype." + type.name() + ".name");
	}

	public static String weaponStatTranslateToLocal(IWeaponStat type) {
		return translateToLocal("weaponstat." + type.getClass() + ".name");
	}

	public static String toInfo(UpgradeTypes type, double value, boolean good) {
		String info = "";
		if (good) {
			info += ChatFormatting.GREEN;
		} else {
			info += ChatFormatting.RED;
		}
		DecimalFormat format = new DecimalFormat("##");
		info += translateToLocal(type) + ": ";
		info += format.format(value * 100);
		return info + "%";
	}

	public static String weaponStatToInfo(IWeaponStat stat, float value) {
		String info = "";
		if (stat.isPositive(value)) {
			info += ChatFormatting.GREEN;
		} else {
			info += ChatFormatting.RED;
		}
		DecimalFormat format = new DecimalFormat("##");
		info += weaponStatTranslateToLocal(stat) + ": ";
		info += format.format(value * 100);
		return info + "%";
	}

	public static String toInfo(UpgradeTypes type, double value) {
		return toInfo(type, value, getGood(type, value));
	}

	public static boolean getGood(UpgradeTypes type, double value) {
		switch (type) {
		case Speed:
			return value < 1;
		case PowerUsage:
			return value < 1;
		case Fail:
			return value < 1;
		default:
			return value >= 1;
		}
	}

	public static String readTextFile(ResourceLocation location) {
		StringBuilder text = new StringBuilder();
		try {
			String path = "/assets/" + location.getNamespace() + "/" + location.getPath();
			InputStream descriptionStream = MOStringHelper.class.getResourceAsStream(path);
			if (descriptionStream == null)
				return text.toString();
			LineNumberReader descriptionReader = new LineNumberReader(new InputStreamReader(descriptionStream));
			String line;

			while ((line = descriptionReader.readLine()) != null) {
				text.append(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return text.toString();
	}

	public static String addPrefix(String name, String prefix) {
		if (prefix.endsWith("-")) {
			return prefix.substring(0, prefix.length() - 2) + Character.toLowerCase(name.charAt(0)) + name.substring(1);
		} else {
			return prefix + " " + name;
		}
	}

	public static String addSuffix(String name, String suffix) {
		if (suffix.startsWith("-")) {
			return name + suffix.substring(1);
		} else {
			return name + " " + suffix;
		}
	}

	public static String[] formatVariations(String unlocalizedName, String unlocalizedSuffix, int count) {
		String[] variations = new String[count];
		for (int i = 0; i < count; i++) {
			variations[i] = unlocalizedName + "." + i + "." + unlocalizedSuffix;
		}
		return variations;
	}

	public static boolean isControlKeyDown() {
		long windowHandle = Minecraft.getInstance().getWindow().getWindow();
		return GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS ||
				GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_RIGHT_CONTROL) == GLFW.GLFW_PRESS;
	}

	public static boolean isAltKeyDown() {
		long windowHandle = Minecraft.getInstance().getWindow().getWindow();
		return GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_LEFT_ALT) == GLFW.GLFW_PRESS ||
				GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_RIGHT_ALT) == GLFW.GLFW_PRESS;
	}
}
