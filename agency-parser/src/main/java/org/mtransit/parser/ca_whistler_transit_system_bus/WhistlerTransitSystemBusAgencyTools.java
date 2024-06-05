package org.mtransit.parser.ca_whistler_transit_system_bus;

import static org.mtransit.commons.StringUtils.EMPTY;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mtransit.commons.CleanUtils;
import org.mtransit.parser.ColorUtils;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.MTLog;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.mt.data.MAgency;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

// https://www.bctransit.com/open-data
public class WhistlerTransitSystemBusAgencyTools extends DefaultAgencyTools {

	public static void main(@NotNull String[] args) {
		new WhistlerTransitSystemBusAgencyTools().start(args);
	}

	@Nullable
	@Override
	public List<Locale> getSupportedLanguages() {
		return LANG_EN;
	}

	@Override
	public boolean defaultExcludeEnabled() {
		return true;
	}

	@NotNull
	@Override
	public String getAgencyName() {
		return "Whistler TS";
	}

	@NotNull
	@Override
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_BUS;
	}

	@Override
	public boolean defaultRouteIdEnabled() {
		return true;
	}

	@Override
	public boolean useRouteShortNameForRouteId() {
		return false; // route ID used by GTFS-RT
	}

	@Override
	public @Nullable String getRouteIdCleanupRegex() {
		return "\\-[A-Z]+$";
	}

	@Override
	public boolean tryRouteDescForMissingLongName() {
		return true;
	}

	@Override
	public boolean defaultRouteLongNameEnabled() {
		return true;
	}

	@NotNull
	@Override
	public String cleanRouteLongName(@NotNull String routeLongName) {
		routeLongName = CleanUtils.cleanSlashes(routeLongName);
		routeLongName = CleanUtils.cleanNumbers(routeLongName);
		routeLongName = CleanUtils.cleanStreetTypes(routeLongName);
		return CleanUtils.cleanLabel(routeLongName);
	}

	@Override
	public boolean defaultAgencyColorEnabled() {
		return true;
	}

	private static final String AGENCY_COLOR_GREEN = "34B233";// GREEN (from PDF Corporate Graphic Standards)
	// private static final String AGENCY_COLOR_BLUE = "002C77"; // BLUE (from PDF Corporate Graphic Standards)

	private static final String AGENCY_COLOR = AGENCY_COLOR_GREEN;

	@NotNull
	@Override
	public String getAgencyColor() {
		return AGENCY_COLOR;
	}

	@Nullable
	@Override
	public String fixColor(@Nullable String color) {
		if (ColorUtils.BLACK.equals(color)) {
			return null; // ignore black
		}
		return super.fixColor(color);
	}

	@Nullable
	@Override
	public String provideMissingRouteColor(@NotNull GRoute gRoute) {
		switch (gRoute.getRouteShortName()) {
		case "4":
			return "00A84F";
		case "5":
			return "8D0B3A";
		case "6":
			return "FFC10E";
		case "7":
			return "B2A97E";
		case "8":
			return "F399C0";
		case "10":
			return "8077B8";
		case "20":
		case "20X":
			return "004B8D";
		case "21":
			return "F7921E";
		case "25":
		case "25X":
			return "EC1A8D";
		case "30":
			return "00ADEE";
		case "31":
			return "A54499";
		case "32":
			return "8BC53F";
		case "99":
			return "5D86A0";
		default:
			throw new MTLog.Fatal("Unexpected route color %s!", gRoute);
		}
	}

	@Override
	public boolean directionFinderEnabled() {
		return true;
	}

	private static final Pattern DASH_VIA_ = Pattern.compile("(-via )", Pattern.CASE_INSENSITIVE);
	private static final String DASH_VIA_REPLACEMENT = " via ";

	private static final Pattern FREE_SHUTTLE_SERVICE = Pattern.compile("((^|\\W)(free (service|shuttle))(\\W|$))", Pattern.CASE_INSENSITIVE);
	private static final String FREE_SHUTTLE_SERVICE_REPLACEMENT = "$2" + EMPTY + "$5";

	private static final Pattern ENDS_WITH_FREE_SERVICE = Pattern.compile("( + (free (service|shuttle))$)", Pattern.CASE_INSENSITIVE);

	private static final Pattern EXPRESS_ = Pattern.compile("((^|\\W)(express|exp)(\\W|$))", Pattern.CASE_INSENSITIVE);
	private static final String EXPRESS_REPLACEMENT = "$2" + EMPTY + "$4";

	private static final Pattern ENDS_WITH_DASH = Pattern.compile("(\\s*-+\\s*$)", Pattern.CASE_INSENSITIVE);

	@NotNull
	@Override
	public String cleanTripHeadsign(@NotNull String tripHeadsign) {
		tripHeadsign = FREE_SHUTTLE_SERVICE.matcher(tripHeadsign).replaceAll(FREE_SHUTTLE_SERVICE_REPLACEMENT);
		tripHeadsign = ENDS_WITH_FREE_SERVICE.matcher(tripHeadsign).replaceAll(EMPTY);
		tripHeadsign = EXPRESS_.matcher(tripHeadsign).replaceAll(EXPRESS_REPLACEMENT);
		tripHeadsign = DASH_VIA_.matcher(tripHeadsign).replaceAll(DASH_VIA_REPLACEMENT);
		tripHeadsign = CleanUtils.keepToAndRemoveVia(tripHeadsign);
		tripHeadsign = ENDS_WITH_DASH.matcher(tripHeadsign).replaceAll(EMPTY);
		tripHeadsign = CleanUtils.CLEAN_AND.matcher(tripHeadsign).replaceAll(CleanUtils.CLEAN_AND_REPLACEMENT);
		tripHeadsign = CleanUtils.CLEAN_PARENTHESIS1.matcher(tripHeadsign).replaceAll(CleanUtils.CLEAN_PARENTHESIS1_REPLACEMENT);
		tripHeadsign = CleanUtils.CLEAN_PARENTHESIS2.matcher(tripHeadsign).replaceAll(CleanUtils.CLEAN_PARENTHESIS2_REPLACEMENT);
		tripHeadsign = CleanUtils.cleanSlashes(tripHeadsign);
		tripHeadsign = CleanUtils.cleanStreetTypes(tripHeadsign);
		tripHeadsign = CleanUtils.cleanNumbers(tripHeadsign);
		return CleanUtils.cleanLabel(tripHeadsign);
	}

	private static final Pattern STARTS_WITH_DCOM = Pattern.compile("(^(\\(-DCOM-\\)))", Pattern.CASE_INSENSITIVE);
	private static final Pattern STARTS_WITH_IMPL = Pattern.compile("(^(\\(-IMPL-\\)))", Pattern.CASE_INSENSITIVE);

	@NotNull
	@Override
	public String cleanStopName(@NotNull String gStopName) {
		gStopName = STARTS_WITH_DCOM.matcher(gStopName).replaceAll(EMPTY);
		gStopName = STARTS_WITH_IMPL.matcher(gStopName).replaceAll(EMPTY);
		gStopName = CleanUtils.cleanBounds(gStopName);
		gStopName = CleanUtils.CLEAN_AND.matcher(gStopName).replaceAll(CleanUtils.CLEAN_AND_REPLACEMENT);
		gStopName = CleanUtils.CLEAN_AT.matcher(gStopName).replaceAll(CleanUtils.CLEAN_AT_REPLACEMENT);
		gStopName = CleanUtils.cleanStreetTypes(gStopName);
		gStopName = CleanUtils.cleanNumbers(gStopName);
		return CleanUtils.cleanLabel(gStopName);
	}

	@Override
	public int getStopId(@NotNull GStop gStop) { // used by GTFS-RT
		return super.getStopId(gStop);
	}
}
