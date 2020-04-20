# 云脉ocr扫描身份证获取身份证信息
**寻之川**
**1.0.0**  
**目前这个module没有使用mvvm 后面有时间再改**
*联系方式：qq 732224734*
***  
## 功能介绍 目前只能扫描身份证获取身份证信息，该module可以直接引用，扫描次数不限，功能没与app包名绑定

##使用方式   在点击事件中执行：
            Intent intent = new Intent(getBaseContext(), CameraActivity.class);
            startActivityForResult(intent, 110);

##信息回调 在onActivityResult中
if (resultCode == 200) {
            if (data != null) {
                IdCardInfo idCardInfo = (IdCardInfo) data.getSerializableExtra("idcardinfo");

                if (idCardInfo != null) {
                    try {
                        String res = new String(idCardInfo.getCharInfo(), "gbk");
                        JSONObject json = new JSONObject(res);

                        JSONObject nameJson = (JSONObject) json.get("Name"); //姓名
                        name.setText(nameJson.getString("value"));

                        JSONObject sexJson = (JSONObject) json.get("Sex"); //性别
                        sex.setText(sexJson.getString("value") + getString(R.string.sex));
                        if (getString(R.string.man).equals(sexJson.getString("value"))) {
                            sex.setTag(1);
                        } else {
                            sex.setTag(2);
                        }

                        JSONObject folkJson = (JSONObject) json.get("Folk");//民族
                        String mz = folkJson.getString("value");
                        flok.setText(mz + getString(R.string.zu));
                        try {
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getAssets().open("minzu")));
                            StringBuilder stringBuilder = new StringBuilder();
                            String s = "";
                            while ((s = bufferedReader.readLine()) != null) {
                                stringBuilder.append(s);
                            }

                            JSONObject jsonObject = new JSONObject(stringBuilder.toString());
                            JSONArray jsonArray = jsonObject.getJSONArray("list");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                if (jsonArray.getJSONObject(i).getString("paramValue").contains(mz)) {
                                    flok.setTag(jsonArray.getJSONObject(i).getString("paramCode"));
                                    break;
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        JSONObject birthJson = (JSONObject) json.get("Birt");//出生日期
                        String birthStr = birthJson.getString("value").replace(getString(R.string.year), "-").replace(getString(R.string.month), "-").replace(getString(R.string.day), "");
                        birthday.setText(birthStr);

                        JSONObject cardJson = (JSONObject) json.get("Num");//身份证号码
                        cardid.setText(cardJson.getString("value"));

                        JSONObject addressJson = (JSONObject) json.get("Addr"); //地址
                        cardJson.getString("value");
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        }


