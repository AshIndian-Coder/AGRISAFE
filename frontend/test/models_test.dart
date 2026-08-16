import 'package:agrotrace_portal/core/models.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  group('ApiEnvelope', () {
    test('parses success envelope and exposes data', () {
      final env = ApiEnvelope.fromMap({
        'success': true,
        'status': 200,
        'code': 'SUCCESS',
        'message': 'Success',
        'data': {'access_token': 'abc'},
      });
      expect(env.success, isTrue);
      expect(env.data, isA<Map<String, dynamic>>());
    });

    test('parses error envelope with trace id', () {
      final env = ApiEnvelope.fromMap({
        'success': false,
        'status': 409,
        'code': 'QR_ALREADY_CONSUMED',
        'message': 'QR has already been consumed.',
        'traceId': 'abc-123-def',
      });
      expect(env.success, isFalse);
      expect(env.code, 'QR_ALREADY_CONSUMED');
      expect(env.traceId, 'abc-123-def');
    });
  });

  group('AuthSession', () {
    test('maps snake_case backend fields', () {
      final s = AuthSession.fromMap({
        'access_token': 'AT',
        'refresh_token': 'RT',
        'token_type': 'Bearer',
        'expires_in': 900,
        'user_uuid': 'u-1',
        'user_name': 'Rajesh Kumar Patel',
        'user_type': 'FARMER',
        'role': 'ROLE_FARMER',
      });
      expect(s.accessToken, 'AT');
      expect(s.refreshToken, 'RT');
      expect(s.expiresIn, 900);
      expect(s.userName, 'Rajesh Kumar Patel');
      expect(s.role, 'ROLE_FARMER');
    });
  });

  group('Lot', () {
    test('tolerates missing and stringified numeric fields', () {
      final lot = Lot.fromMap({
        'lotId': 'LOT-26-0001',
        'quantity': '500.5',
        'unit': 'Kg',
        'status': 'CREATED',
        'recalled': false,
      });
      expect(lot.lotId, 'LOT-26-0001');
      expect(lot.quantity, 500.5);
      expect(lot.status, 'CREATED');
      expect(lot.recalled, isFalse);
    });
  });

  group('Paged', () {
    test('parses PagedResponse envelope', () {
      final p = Paged.parse<Lot>({
        'content': [
          {'lotId': 'A', 'status': 'CREATED', 'recalled': false},
          {'lotId': 'B', 'status': 'ACCEPTED', 'recalled': false},
        ],
        'page': 0,
        'size': 20,
        'totalElements': 2,
        'totalPages': 1,
        'first': true,
        'last': true,
        'empty': false,
      }, Lot.fromMap);
      expect(p.content.length, 2);
      expect(p.totalElements, 2);
      expect(p.empty, isFalse);
    });

    test('also accepts bare JSON lists', () {
      final p = Paged.parse<Lot>([
        {'lotId': 'A', 'status': 'CREATED', 'recalled': false},
      ], Lot.fromMap);
      expect(p.content.length, 1);
      expect(p.empty, isFalse);
    });
  });

  group('Verification', () {
    test('consumer verification verdict mapping', () {
      final v = Verification.fromMap({
        'verificationStatus': 'VERIFIED',
        'productName': 'Milk',
        'manufacturer': 'Government Verified Producer',
        'qualityStatus': 'PASSED',
        'traceabilityComplete': true,
        'retailerReceived': true,
        'recalled': false,
        'traceEventCount': 12,
      });
      expect(v.verificationStatus, 'VERIFIED');
      expect(v.traceEventCount, 12);
      expect(v.recalled, isFalse);
    });
  });
}
