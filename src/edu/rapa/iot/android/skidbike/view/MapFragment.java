package edu.rapa.iot.android.skidbike.view;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;
import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import edu.rapa.iot.android.skidbike.R;
import edu.rapa.iot.android.skidbike.controler.FacilityDAO;
import edu.rapa.iot.android.skidbike.util.Constants;

public class MapFragment extends Fragment implements OnClickListener {
	
	/*
	 * SpeedActivity에서 Fragment하게 되는 지도 부분입니다.
	 * DaumAPI를 이용하여 구현하였으며, 내부 DB의 facility 테이블의 값을 불러와
	 * 커스텀 마커로 위치를 뿌려주는 기능이 있습니다.
	 * 
	 *  1. 현재위치 구현 
	 *   - 다음API에서 지원하는 currentLocationTrackingMode 메소드를 통해
	 *     현재 위치 트래킹을 on/off 하게 됩니다.
	 *  2. 시설물 마킹
	 *   - facility db에 있는 데이터를 카테고리별로 로딩해온뒤
	 *     lon, lat 변수의 값에 따라 그려주게 됩니다.
	 *   - 현재 버전에는 1808개의 값을 읽어옴에 따라 AsyncTask를 이용하여
	 *     값을 불러오게 됩니다.
	 *   - AsyncTask의 파라미터는 String배열로 넘겨주며, 카테고리명, 해당 시설물 로고 파일명,
	 *    시설물 명을 넘겨주어 AsyncTask에서 Marker를 set해줄때 사용합니다.
	 *
	 */
	
	
	
	
	
	
	
	
	
	private MapView mapView;
	Activity myActivity;
	MapPolyline polyline;
	MapPOIItem marker;
	FacilityDAO fDAO;
	Cursor c;

	private static final int TRACKINGMODE_ON = 1;
	private static final int TRACKINGMODE_OFF = 2;
	private static final int DRAWMODE_ON = 1;
	private static final int DRAWMODE_OFF = 2;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		myActivity = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View myView = inflater.inflate(R.layout.fragment_map, null);
		mapView = new MapView(myActivity);

		mapView.setDaumMapApiKey(Constants.API_KEY);

		marker = new MapPOIItem();
		polyline = new MapPolyline();
		polyline.setTag(1000);
		polyline.setLineColor(Color.argb(128, 255, 51, 0));

		myView.findViewById(R.id.map_Fragment_LocationOnOff_Btn)
				.setOnClickListener(this);
		myView.findViewById(R.id.map_facility_Btn).setOnClickListener(this);
		myView.findViewById(R.id.map_rental_Btn).setOnClickListener(this);
		myView.findViewById(R.id.map_Repair_Btn).setOnClickListener(this);
		myView.findViewById(R.id.map_Road_Btn).setOnClickListener(this);
		myView.findViewById(R.id.map_Storage_Btn).setOnClickListener(this);

		myView.findViewById(R.id.map_Fragment_LocationOnOff_Btn)
				.setOnClickListener(new OnClickListener() {

					int trackingStatus = TRACKINGMODE_OFF;

					@Override
					public void onClick(View v) {
						mapView.removeAllPOIItems();
						switch (trackingStatus) {
						case TRACKINGMODE_OFF:
							mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
							trackingStatus = TRACKINGMODE_ON;
							v.setAlpha(1);
							break;

						case TRACKINGMODE_ON:
							mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
							trackingStatus = TRACKINGMODE_OFF;
							v.setAlpha(0.35f);
							break;
						}

						Log.d("onclick", "CLICKED!!!!!!!!!");

					}
				});

		// 보관대
		myView.findViewById(R.id.map_Storage_Btn).setOnClickListener(
				new OnClickListener() {

					int drawMode = DRAWMODE_OFF;

					@Override
					public void onClick(View v) {
						mapView.removeAllPOIItems();
						switch (drawMode) {
						case DRAWMODE_OFF:
							new FacilityLoadingTask().execute(new String[] {
									"시내_보관대", "" + R.drawable.city_storage_50,
									"자전거 보관대" });
							new FacilityLoadingTask().execute(new String[] {
									"한강_보관대", "" + R.drawable.river_storage_50,
									"자전거 보관대" });
							Log.d("draw", "보관함 불러오기");
							drawMode = DRAWMODE_ON;
							v.setAlpha(1);
							break;

							
						case DRAWMODE_ON:
							Log.d("draw", "보관함 불러오기 해제");
							mapView.removeAllPOIItems();
							drawMode = DRAWMODE_OFF;
							v.setAlpha(0.35f);
							break;
						default:
							Log.d("draw", "버튼 오류");
							
							break;

						}

					}
				});
		// 대여소
		myView.findViewById(R.id.map_rental_Btn).setOnClickListener(
				new OnClickListener() {

					int drawMode = DRAWMODE_OFF;

					@Override
					public void onClick(View v) {
						mapView.removeAllPOIItems();
						switch (drawMode) {
						case DRAWMODE_OFF:
							new FacilityLoadingTask().execute(new String[] {
									"시내_대여소", "" + R.drawable.city_rental_50,
									"자전거 대여소" });
							new FacilityLoadingTask().execute(new String[] {
									"한강_대여소", "" + R.drawable.river_rental_50,
									"자전거 대여소" });
							Log.d("draw", "보관함 불러오기");
							drawMode = DRAWMODE_ON;
							v.setAlpha(1);
							break;

						case DRAWMODE_ON:
							Log.d("draw", "보관함 불러오기 해제");
							mapView.removeAllPOIItems();
							drawMode = DRAWMODE_OFF;
							v.setAlpha(0.35f);
							break;
						default:
							Log.d("draw", "버튼 오류");
							break;

						}

					}
				});
		// 수리
		myView.findViewById(R.id.map_Repair_Btn).setOnClickListener(
				new OnClickListener() {

					int drawMode = DRAWMODE_OFF;

					@Override
					public void onClick(View v) {
						mapView.removeAllPOIItems();
						switch (drawMode) {
						case DRAWMODE_OFF:

							new FacilityLoadingTask().execute(new String[] {
									"시내_펌프", "" + R.drawable.city_pump_50,
									"공기주입기" });
							new FacilityLoadingTask().execute(new String[] {
									"한강_펌프", "" + R.drawable.river_pump_50,
									"공기주입기" });
							new FacilityLoadingTask().execute(new String[] {
									"시내_매장", "" + R.drawable.city_shop_50,
									"자전거수리점" });
							new FacilityLoadingTask().execute(new String[] {
									"한강_매장", "" + R.drawable.river_shop_50,
									"자전거수리점" });
							Log.d("draw", "보관함 불러오기");
							drawMode = DRAWMODE_ON;
							v.setAlpha(1);
							break;

						case DRAWMODE_ON:
							Log.d("draw", "보관함 불러오기 해제");
							mapView.removeAllPOIItems();
							drawMode = DRAWMODE_OFF;
							v.setAlpha(0.35f);
							break;
						default:
							Log.d("draw", "버튼 오류");
							break;

						}

					}
				});
		// 편의시설
		myView.findViewById(R.id.map_facility_Btn).setOnClickListener(
				new OnClickListener() {

					int drawMode = DRAWMODE_OFF;

					@Override
					public void onClick(View v) {
						mapView.removeAllPOIItems();
						switch (drawMode) {
						case DRAWMODE_OFF:
							new FacilityLoadingTask().execute(new String[] {
									"시내_화장실", "" + R.drawable.city_toliet_50,
									"화장실" });
							new FacilityLoadingTask().execute(new String[] {
									"한강_화장실", "" + R.drawable.river_toliet_50,
									"화장실" });
							new FacilityLoadingTask().execute(new String[] {
									"한강_매점", "" + R.drawable.river_convini_50,
									"편의점" });
							new FacilityLoadingTask().execute(new String[] {
									"한강_식수대", "" + R.drawable.river_water_48,
									"음수대" });
							Log.d("draw", "보관함 불러오기");
							drawMode = DRAWMODE_ON;
							v.setAlpha(1);
							break;

						case DRAWMODE_ON:
							Log.d("draw", "보관함 불러오기 해제");
							mapView.removeAllPOIItems();
							drawMode = DRAWMODE_OFF;
							v.setAlpha(0.35f);
							break;
						default:
							Log.d("draw", "버튼 오류");
							break;

						}

					}
				});
		// 진출입
		myView.findViewById(R.id.map_Road_Btn).setOnClickListener(
				new OnClickListener() {

					int drawMode = DRAWMODE_OFF;

					@Override
					public void onClick(View v) {
						mapView.removeAllPOIItems();
						switch (drawMode) {
						case DRAWMODE_OFF:
							new FacilityLoadingTask()
									.execute(new String[] { "한강_진출입_계단",
											"" + R.drawable.river_stair_50,
											"한강진입로(계단)" });
							new FacilityLoadingTask().execute(new String[] {
									"한강_지천다리", "" + R.drawable.river_bridge_50,
									"다리" });
							new FacilityLoadingTask()
									.execute(new String[] { "한강_엘리베이터",
											"" + R.drawable.river_elevator_50,
											"엘리베이터" });
							new FacilityLoadingTask()
									.execute(new String[] { "한강_진출입로_경사",
											"" + R.drawable.river_slop_50,
											"한강집입로(경사)" });
							Log.d("draw", "보관함 불러오기");
							drawMode = DRAWMODE_ON;
							v.setAlpha(1);
							break;

						case DRAWMODE_ON:
							Log.d("draw", "보관함 불러오기 해제");
							mapView.removeAllPOIItems();
							drawMode = DRAWMODE_OFF;
							v.setAlpha(0.35f);
							break;
						default:
							Log.d("draw", "버튼 오류");
							break;

						}

					}
				});

		ViewGroup mapViewContainer = (ViewGroup) myView
				.findViewById(R.id.map_view);
		mapViewContainer.addView(mapView);

		return myView;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	private class FacilityLoadingTask extends AsyncTask<String, String, Cursor> {

		@Override
		protected Cursor doInBackground(String... params) {
			fDAO = new FacilityDAO(myActivity);
			fDAO.open();

			c = fDAO.search(params[0]);
			while (c.moveToNext()) {

				String category = c.getString(c.getColumnIndex("category"));
				double lat = c.getDouble(c.getColumnIndex("latitude"));
				double lon = c.getDouble(c.getColumnIndex("longitude"));

				MapPOIItem marker = new MapPOIItem();
				marker.setItemName(params[2]);
				marker.setTag(0);
				marker.setMapPoint(MapPoint.mapPointWithGeoCoord(lat, lon));
				marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
				marker.setCustomImageResourceId(Integer.parseInt(params[1]));
				marker.setCustomImageAutoscale(true);
				marker.setCustomImageAnchor(0.5f, 1.0f);

				mapView.addPOIItem(marker);
			}

			fDAO.close();
			return null;
		}

	}

	public void changeCenterPointer(double lat, double lon) {
		mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(lat, lon), true);
	}

	public void traceSkid(double lat, double lon) {
		// Polyline 좌표 지정.
		polyline.addPoint(MapPoint.mapPointWithGeoCoord(lat, lon));

		// Polyline 지도에 올리기.
		mapView.addPolyline(polyline);

		marker.setItemName("Default Marker");
		marker.setTag(0);
		marker.setMapPoint(MapPoint.mapPointWithGeoCoord(lat, lon));

		marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
		marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
		MapPointBounds mapPointBounds = new MapPointBounds(
				polyline.getMapPoints());
		int padding = 100; // px
		mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(
				mapPointBounds, padding));
		mapView.addPOIItem(marker);
	}

}
