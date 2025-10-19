# SonarQube Quality Gate Configuration

## 🎯 Recommended Quality Gate Settings

### **Coverage Thresholds**
- **Line Coverage**: ≥ 80%
- **Branch Coverage**: ≥ 70%
- **Overall Coverage**: ≥ 80%

### **Duplication**
- **Duplicated Lines**: ≤ 3%
- **Duplicated Blocks**: ≤ 1%

### **Maintainability**
- **Code Smells**: ≤ 100
- **Technical Debt**: ≤ 2 hours
- **Maintainability Rating**: A

### **Reliability**
- **Bugs**: 0
- **Reliability Rating**: A

### **Security**
- **Vulnerabilities**: 0
- **Security Hotspots**: ≤ 5
- **Security Rating**: A

### **Size**
- **Lines of Code**: ≤ 10,000 per module
- **Files**: ≤ 100 per module

## 🔧 SonarCloud Quality Gate Setup

1. **Go to SonarCloud** → Your Project → Administration → Quality Gates
2. **Create New Quality Gate**: "Animal Shelter Microservices"
3. **Configure Conditions**:

| Condition | Operator | Value |
|-----------|----------|-------|
| Coverage | is less than | 80% |
| Duplicated Lines (%) | is greater than | 3% |
| Maintainability Rating | is worse than | A |
| Reliability Rating | is worse than | A |
| Security Rating | is worse than | A |
| Security Hotspots | is greater than | 5 |
| Bugs | is greater than | 0 |
| Vulnerabilities | is greater than | 0 |

4. **Set as Default** for your project

## 📊 Coverage Exclusions

The following are automatically excluded from coverage:
- Configuration classes
- DTOs and Entities
- Generated code
- Test utilities
- Main application classes

## 🚨 Quality Gate Behavior

- **PASS**: All conditions met → Pipeline continues
- **FAIL**: Any condition failed → Pipeline fails
- **WARN**: Non-blocking issues → Pipeline continues with warnings
