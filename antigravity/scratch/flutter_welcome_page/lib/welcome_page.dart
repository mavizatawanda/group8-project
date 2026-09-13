import 'package:flutter/material.dart';

/// A modern, responsive Welcome Page featuring a sky-blue header.
/// Suitable for mobile, tablet, and web Flutter applications.
class WelcomePage extends StatelessWidget {
  const WelcomePage({super.key});

  // Sky Blue Theme Palette
  static const Color skyBlue = Color(0xFF87CEEB);
  static const Color skyBlueDark = Color(0xFF5BA4CF);
  static const Color skyBlueLight = Color(0xFFE6F4FA);
  static const Color primaryNavy = Color(0xFF0F172A);
  static const Color textMuted = Color(0xFF64748B);

  @override
  Widget build(BuildContext context) {
    final size = MediaQuery.of(context).size;
    final isCompact = size.width < 600;

    return Scaffold(
      backgroundColor: Colors.white,
      body: SafeArea(
        top: false, // Allows the skyblue header to extend into the status bar
        child: SingleChildScrollView(
          physics: const BouncingScrollPhysics(),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              // ==========================================================
              // 1. HEADER SECTION WITH SKY BLUE BACKGROUND
              // ==========================================================
              _buildSkyBlueHeader(context, isCompact),

              const SizedBox(height: 28),

              // ==========================================================
              // 2. MAIN BODY / FEATURES
              // ==========================================================
              Padding(
                padding: EdgeInsets.symmetric(
                  horizontal: isCompact ? 20.0 : 48.0,
                ),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text(
                      'Explore Key Features',
                      style: TextStyle(
                        fontSize: 20,
                        fontWeight: FontWeight.bold,
                        color: primaryNavy,
                        letterSpacing: -0.3,
                      ),
                    ),
                    const SizedBox(height: 16),

                    _buildFeatureCard(
                      icon: Icons.verified_user_rounded,
                      iconColor: Color(0xFF0284C7),
                      iconBg: Color(0xFFE0F2FE),
                      title: 'Instant Verification',
                      description:
                          'Verify academic and professional certificates in seconds with cryptographically secure records.',
                    ),
                    const SizedBox(height: 14),

                    _buildFeatureCard(
                      icon: Icons.shield_rounded,
                      iconColor: Color(0xFF0D9488),
                      iconBg: Color(0xFFCCFBF1),
                      title: 'Tamper-Evident Ledger',
                      description:
                          'Zero-trust integrity checking with SHA-256 cryptographic fingerprints and audit trail.',
                    ),
                    const SizedBox(height: 14),

                    _buildFeatureCard(
                      icon: Icons.speed_rounded,
                      iconColor: Color(0xFF7C3AED),
                      iconBg: Color(0xFFEDE9FE),
                      title: 'DevOps & Multi-Cloud Ready',
                      description:
                          'Automated quality gates and 99.9% high availability with seamless cloud sync.',
                    ),

                    const SizedBox(height: 36),

                    // ==========================================================
                    // 3. ACTION BUTTONS (GET STARTED & SIGN IN)
                    // ==========================================================
                    SizedBox(
                      width: double.infinity,
                      height: 54,
                      child: ElevatedButton(
                        onPressed: () {
                          // Handle Get Started / Sign Up navigation
                          ScaffoldMessenger.of(context).showSnackBar(
                            const SnackBar(content: Text('Navigating to Get Started...')),
                          );
                        },
                        style: ElevatedButton.styleFrom(
                          backgroundColor: const Color(0xFF0284C7),
                          foregroundColor: Colors.white,
                          elevation: 2,
                          shadowColor: const Color(0xFF0284C7).withOpacity(0.35),
                          shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(16),
                          ),
                        ),
                        child: const Row(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            Text(
                              'Get Started',
                              style: TextStyle(
                                fontSize: 16,
                                fontWeight: FontWeight.w700,
                                letterSpacing: 0.2,
                              ),
                            ),
                            SizedBox(width: 8),
                            Icon(Icons.arrow_forward_rounded, size: 20),
                          ],
                        ),
                      ),
                    ),
                    const SizedBox(height: 12),

                    SizedBox(
                      width: double.infinity,
                      height: 52,
                      child: OutlinedButton(
                        onPressed: () {
                          // Handle Sign In navigation
                          ScaffoldMessenger.of(context).showSnackBar(
                            const SnackBar(content: Text('Navigating to Sign In...')),
                          );
                        },
                        style: OutlinedButton.styleFrom(
                          foregroundColor: primaryNavy,
                          side: const BorderSide(color: Color(0xFFCBD5E1), width: 1.5),
                          shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(16),
                          ),
                        ),
                        child: const Text(
                          'I already have an account • Sign In',
                          style: TextStyle(
                            fontSize: 15,
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                      ),
                    ),

                    const SizedBox(height: 32),

                    // Footer note
                    Center(
                      child: Text(
                        'Secure • Compliant • Trusted by Institutions',
                        style: TextStyle(
                          fontSize: 12,
                          color: textMuted.withOpacity(0.8),
                          fontWeight: FontWeight.w500,
                        ),
                      ),
                    ),
                    const SizedBox(height: 24),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  /// Builds the top header with a vibrant sky-blue background and curved bottom.
  Widget _buildSkyBlueHeader(BuildContext context, bool isCompact) {
    return Container(
      width: double.infinity,
      padding: EdgeInsets.only(
        top: MediaQuery.of(context).padding.top + 24,
        left: isCompact ? 24.0 : 48.0,
        right: isCompact ? 24.0 : 48.0,
        bottom: 36.0,
      ),
      decoration: const BoxDecoration(
        color: skyBlue, // Sky Blue background
        gradient: LinearGradient(
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
          colors: [
            Color(0xFF87CEEB), // Sky blue
            Color(0xFF67BFE8), // Slightly deeper sky blue
            Color(0xFF4FAADB), // Accent sky blue
          ],
        ),
        borderRadius: BorderRadius.only(
          bottomLeft: Radius.circular(32),
          bottomRight: Radius.circular(32),
        ),
        boxShadow: [
          BoxShadow(
            color: Color(0x334FAADB),
            blurRadius: 18,
            offset: Offset(0, 10),
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Top Navigation / Brand Row
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Container(
                padding: const EdgeInsets.all(10),
                decoration: BoxDecoration(
                  color: Colors.white.withOpacity(0.9),
                  borderRadius: BorderRadius.circular(14),
                  boxShadow: [
                    BoxShadow(
                      color: Colors.black.withOpacity(0.06),
                      blurRadius: 10,
                      offset: const Offset(0, 4),
                    ),
                  ],
                ),
                child: const Icon(
                  Icons.school_rounded,
                  color: Color(0xFF0369A1),
                  size: 28,
                ),
              ),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                decoration: BoxDecoration(
                  color: Colors.white.withOpacity(0.35),
                  borderRadius: BorderRadius.circular(20),
                  border: Border.pad(
                    Border(
                      top: BorderSide(color: Colors.white.withOpacity(0.6)),
                      bottom: BorderSide(color: Colors.white.withOpacity(0.3)),
                    ),
                  ),
                ),
                child: const Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Icon(Icons.shield_outlined, size: 16, color: primaryNavy),
                    SizedBox(width: 6),
                    Text(
                      'v2.0 Verified',
                      style: TextStyle(
                        fontSize: 12,
                        fontWeight: FontWeight.w700,
                        color: primaryNavy,
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),

          const SizedBox(height: 28),

          // Greeting & Title
          const Text(
            'Welcome to QVS',
            style: TextStyle(
              fontSize: 15,
              fontWeight: FontWeight.w600,
              color: Color(0xFF0F3E61),
              letterSpacing: 0.8,
            ),
          ),
          const SizedBox(height: 6),
          const Text(
            'Academic &\nProfessional Verification',
            style: TextStyle(
              fontSize: 30,
              fontWeight: FontWeight.w800,
              color: primaryNavy,
              height: 1.15,
              letterSpacing: -0.5,
            ),
          ),
          const SizedBox(height: 12),
          Text(
            'Fast, cryptographically certified validation for degrees, diplomas, and official accreditations.',
            style: TextStyle(
              fontSize: 14,
              color: const Color(0xFF0F3E61).withOpacity(0.9),
              height: 1.45,
            ),
          ),
        ],
      ),
    );
  }

  /// Feature highlight item card
  Widget _buildFeatureCard({
    required IconData icon,
    required Color iconColor,
    required Color iconBg,
    required String title,
    required String description,
  }) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: const Color(0xFFF8FAFC),
        borderRadius: BorderRadius.circular(18),
        border: Border.all(color: const Color(0xFFE2E8F0)),
      ),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Container(
            padding: const EdgeInsets.all(10),
            decoration: BoxDecoration(
              color: iconBg,
              borderRadius: BorderRadius.circular(12),
            ),
            child: Icon(icon, color: iconColor, size: 24),
          ),
          const SizedBox(width: 14),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  title,
                  style: const TextStyle(
                    fontSize: 15,
                    fontWeight: FontWeight.w700,
                    color: primaryNavy,
                  ),
                ),
                const SizedBox(height: 4),
                Text(
                  description,
                  style: const TextStyle(
                    fontSize: 13,
                    color: textMuted,
                    height: 1.4,
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
