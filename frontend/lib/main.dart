import 'package:flutter/material.dart';

import 'core/api_client.dart';
import 'core/deps.dart';
import 'services/agro_api.dart';
import 'ui/app.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();

  deps.config = await AppConfig.init();
  deps.session = Session();
  deps.client = ApiClient(deps.session);
  deps.api = AgroTraceApi(deps.client);

  runApp(const AgroTraceApp());
}
