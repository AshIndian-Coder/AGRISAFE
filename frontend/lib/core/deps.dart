import 'api_client.dart';
import '../services/agro_api.dart';

/// Service locator — initialised once in `main()`, then shared app-wide.
class AppDeps {
  late AppConfig config;
  late Session session;
  late ApiClient client;
  late AgroTraceApi api;
}

final AppDeps deps = AppDeps();
