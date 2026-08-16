/// The five supply-chain nodes of the AgroTrace grid, mapped onto the
/// backend's user types and seeded demo identities.
library;

class NodeRoleMeta {
  final String nodeCode;
  final String emoji;
  final String title;
  final String tagline;
  final String description;
  final String ministry;
  final String demoIdentity;
  final String backendUserType;
  final bool canRegister;
  final String identityLabel;
  final String identityHint;

  const NodeRoleMeta({
    required this.nodeCode,
    required this.emoji,
    required this.title,
    required this.tagline,
    required this.description,
    required this.ministry,
    required this.demoIdentity,
    required this.backendUserType,
    required this.canRegister,
    required this.identityLabel,
    required this.identityHint,
  });
}

enum NodeRole {
  farmer(
    NodeRoleMeta(
      nodeCode: 'NODE-01',
      emoji: '🌾',
      title: 'Farmer / Producer',
      tagline: 'Origin node — creates the first traceability record',
      description:
          'Register harvest lots, log produce weight and declare farm-gate value.',
      ministry: 'Dept. of Agriculture & Farmers Welfare',
      demoIdentity: 'AADHAR-DEMO-FARMER',
      backendUserType: 'FARMER',
      canRegister: true,
      identityLabel: 'Aadhaar Reference (Registry)',
      identityHint: 'e.g. AADHAR-DEMO-FARMER',
    ),
  ),
  agent(
    NodeRoleMeta(
      nodeCode: 'NODE-02',
      emoji: '🏭',
      title: 'Receiving Agent',
      tagline: 'Intake node — validates and grades incoming produce',
      description:
          'Scan farm batches into storage, log cold-chain telemetry and quality scores.',
      ministry: 'Warehousing Development & Regulatory Authority',
      demoIdentity: 'PF-COL-DEMO',
      backendUserType: 'COLLECTION_AGENT',
      canRegister: true,
      identityLabel: 'PF Reference (Registry)',
      identityHint: 'e.g. PF-COL-DEMO',
    ),
  ),
  supplier(
    NodeRoleMeta(
      nodeCode: 'NODE-03',
      emoji: '🚚',
      title: 'Middleman / Aggregator',
      tagline: 'Logistics node — moves consignments between hubs',
      description:
          'Raise dispatch orders, assign vehicles and track in-transit consignments.',
      ministry: 'APMC / State Marketing Board',
      demoIdentity: 'PF-SUP-DEMO',
      backendUserType: 'SUPPLIER',
      canRegister: true,
      identityLabel: 'PF Reference (Registry)',
      identityHint: 'e.g. PF-SUP-DEMO',
    ),
  ),
  retailer(
    NodeRoleMeta(
      nodeCode: 'NODE-04',
      emoji: '🏪',
      title: 'Retailer / Business',
      tagline: 'Shelf node — publishes the consumer-facing trace QR',
      description:
          'Stock inventory, price SKUs and generate scannable retail trace labels.',
      ministry: 'Ministry of Consumer Affairs · Legal Metrology',
      demoIdentity: 'AADHAR-DEMO-RET',
      backendUserType: 'RETAILER',
      canRegister: true,
      identityLabel: 'GST / Aadhaar Reference (Registry)',
      identityHint: 'e.g. AADHAR-DEMO-RET',
    ),
  ),
  inspector(
    NodeRoleMeta(
      nodeCode: 'NODE-05',
      emoji: '🛡️',
      title: 'FSSAI Inspector',
      tagline: 'Regulatory node — audits and enforces food safety',
      description:
          'File lab-backed compliance audits, clear or reject lots and record violations.',
      ministry: 'Food Safety and Standards Authority of India',
      demoIdentity: 'PF-AG-DEMO',
      backendUserType: 'GOVERNMENT_EMPLOYEE',
      canRegister: false,
      identityLabel: 'Officer PF Reference (Registry)',
      identityHint: 'e.g. PF-AG-DEMO',
    ),
  );

  const NodeRole(this.meta);

  final NodeRoleMeta meta;
}
