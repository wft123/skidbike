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
	 * SpeedActivity���� Fragment�ϰ� �Ǵ� ���� �κ��Դϴ�.
	 * DaumAPI�� �̿��Ͽ� �����Ͽ�����, ���� DB�� facility ���̺��� ���� �ҷ���
	 * Ŀ���� ��Ŀ�� ��ġ�� �ѷ��ִ� ����� �ֽ��ϴ�.
	 * 
	 *  1. ������ġ ���� 
	 *   - ����API���� �����ϴ� currentLocationTrackingMode �޼ҵ带 ����
	 *     ���� ��ġ Ʈ��ŷ�� on/off �ϰ� �˴ϴ�.
	 *  2. �ü��� ��ŷ
	 *   - facility db�� �ִ� �����͸� ī�װ����� �ε��ؿµ�
	 *     lon, lat ������ ���� ���� �׷��ְ� �˴ϴ�.
	 *   - ���� �������� 1808���� ���� �о�ȿ� ���� AsyncTask�� �̿��Ͽ�
	 *     ���� �ҷ����� �˴ϴ�.
	 *   - AsyncTask�� �Ķ���ʹ� String�迭�� �Ѱ��ָ�, ī�װ���, �ش� �ü��� �ΰ� ���ϸ�,
	 *    �ü��� ���� �Ѱ��־� AsyncTask���� Marker�� set���ٶ� ����մϴ�.
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

		// ������
		myView.findViewById(R.id.map_Storage_Btn).setOnClickListener(
				new OnClickListener() {

					int drawMode = DRAWMODE_OFF;

					@Override
					public void onClick(View v) {
						mapView.removeAllPOIItems();
						switch (drawMode) {
						case DRAWMODE_OFF:
							new FacilityLoadingTask().execute(new String[] {
									"�ó�_������", "" + R.drawable.city_storage_50,
									"������ ������" });
							new FacilityLoadingTask().execute(new String[] {
									"�Ѱ�_������", "" + R.drawable.river_storage_50,
									"������ ������" });
							Log.d("draw", "������ �ҷ�����");
							drawMode = DRAWMODE_ON;
							v.setAlpha(1);
							break;

							
						case DRAWMODE_ON:
							Log.d("draw", "������ �ҷ����� ����");
							mapView.removeAllPOIItems();
							drawMode = DRAWMODE_OFF;
							v.setAlpha(0.35f);
							break;
						default:
							Log.d("draw", "��ư ����");
							
							break;

						}

					}
				});
		// �뿩��
		myView.findViewById(R.id.map_rental_Btn).setOnClickListener(
				new OnClickListener() {

					int drawMode = DRAWMODE_OFF;

					@Override
					public void onClick(View v) {
						mapView.removeAllPOIItems();
						switch (drawMode) {
						case DRAWMODE_OFF:
							new FacilityLoadingTask().execute(new String[] {
									"�ó�_�뿩��", "" + R.drawable.city_rental_50,
									"������ �뿩��" });
							new FacilityLoadingTask().execute(new String[] {
									"�Ѱ�_�뿩��", "" + R.drawable.river_rental_50,
									"������ �뿩��" });
							Log.d("draw", "������ �ҷ�����");
							drawMode = DRAWMODE_ON;
							v.setAlpha(1);
							break;

						case DRAWMODE_ON:
							Log.d("draw", "������ �ҷ����� ����");
							mapView.removeAllPOIItems();
							drawMode = DRAWMODE_OFF;
							v.setAlpha(0.35f);
							break;
						default:
							Log.d("draw", "��ư ����");
							break;

						}

					}
				});
		// ����
		myView.findViewById(R.id.map_Repair_Btn).setOnClickListener(
				new OnClickListener() {

					int drawMode = DRAWMODE_OFF;

					@Override
					public void onClick(View v) {
						mapView.removeAllPOIItems();
						switch (drawMode) {
						case DRAWMODE_OFF:

							new FacilityLoadingTask().execute(new String[] {
									"�ó�_����", "" + R.drawable.city_pump_50,
									"�������Ա�" });
							new FacilityLoadingTask().execute(new String[] {
									"�Ѱ�_����", "" + R.drawable.river_pump_50,
									"�������Ա�" });
							new FacilityLoadingTask().execute(new String[] {
									"�ó�_����", "" + R.drawable.city_shop_50,
									"�����ż�����" });
							new FacilityLoadingTask().execute(new String[] {
									"�Ѱ�_����", "" + R.drawable.river_shop_50,
									"�����ż�����" });
							Log.d("draw", "������ �ҷ�����");
							drawMode = DRAWMODE_ON;
							v.setAlpha(1);
							break;

						case DRAWMODE_ON:
							Log.d("draw", "������ �ҷ����� ����");
							mapView.removeAllPOIItems();
							drawMode = DRAWMODE_OFF;
							v.setAlpha(0.35f);
							break;
						default:
							Log.d("draw", "��ư ����");
							break;

						}

					}
				});
		// ���ǽü�
		myView.findViewById(R.id.map_facility_Btn).setOnClickListener(
				new OnClickListener() {

					int drawMode = DRAWMODE_OFF;

					@Override
					public void onClick(View v) {
						mapView.removeAllPOIItems();
						switch (drawMode) {
						case DRAWMODE_OFF:
							new FacilityLoadingTask().execute(new String[] {
									"�ó�_ȭ���", "" + R.drawable.city_toliet_50,
									"ȭ���" });
							new FacilityLoadingTask().execute(new String[] {
									"�Ѱ�_ȭ���", "" + R.drawable.river_toliet_50,
									"ȭ���" });
							new FacilityLoadingTask().execute(new String[] {
									"�Ѱ�_����", "" + R.drawable.river_convini_50,
									"������" });
							new FacilityLoadingTask().execute(new String[] {
									"�Ѱ�_�ļ���", "" + R.drawable.river_water_48,
									"������" });
							Log.d("draw", "������ �ҷ�����");
							drawMode = DRAWMODE_ON;
							v.setAlpha(1);
							break;

						case DRAWMODE_ON:
							Log.d("draw", "������ �ҷ����� ����");
							mapView.removeAllPOIItems();
							drawMode = DRAWMODE_OFF;
							v.setAlpha(0.35f);
							break;
						default:
							Log.d("draw", "��ư ����");
							break;

						}

					}
				});
		// ������
		myView.findViewById(R.id.map_Road_Btn).setOnClickListener(
				new OnClickListener() {

					int drawMode = DRAWMODE_OFF;

					@Override
					public void onClick(View v) {
						mapView.removeAllPOIItems();
						switch (drawMode) {
						case DRAWMODE_OFF:
							new FacilityLoadingTask()
									.execute(new String[] { "�Ѱ�_������_���",
											"" + R.drawable.river_stair_50,
											"�Ѱ����Է�(���)" });
							new FacilityLoadingTask().execute(new String[] {
									"�Ѱ�_��õ�ٸ�", "" + R.drawable.river_bridge_50,
									"�ٸ�" });
							new FacilityLoadingTask()
									.execute(new String[] { "�Ѱ�_����������",
											"" + R.drawable.river_elevator_50,
											"����������" });
							new FacilityLoadingTask()
									.execute(new String[] { "�Ѱ�_�����Է�_���",
											"" + R.drawable.river_slop_50,
											"�Ѱ����Է�(���)" });
							Log.d("draw", "������ �ҷ�����");
							drawMode = DRAWMODE_ON;
							v.setAlpha(1);
							break;

						case DRAWMODE_ON:
							Log.d("draw", "������ �ҷ����� ����");
							mapView.removeAllPOIItems();
							drawMode = DRAWMODE_OFF;
							v.setAlpha(0.35f);
							break;
						default:
							Log.d("draw", "��ư ����");
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
		// Polyline ��ǥ ����.
		polyline.addPoint(MapPoint.mapPointWithGeoCoord(lat, lon));

		// Polyline ������ �ø���.
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
