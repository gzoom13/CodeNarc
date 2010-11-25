/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codenarc.rule.unnecessary

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule

/**
 * Tests for UnnecessaryObjectReferencesRule
 *
 * @author Hamlet D'Arcy
 * @version $Revision: 329 $ - $Date: 2010-04-29 04:20:25 +0200 (Thu, 29 Apr 2010) $
 */
class UnnecessaryObjectReferencesRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == "UnnecessaryObjectReferences"
    }

    void testSuccessScenario() {
        final SOURCE = '''

                // 'this' reference ignored
                firstName = 'Hamlet'
                lastName = "D'Arcy"
                employer = 'Canoo'
                street = 'Kirschgaraten 5'
                city = 'Basel'
                zipCode = '4051'

                // only 5 references
                def p = new Person()
                p.firstName = 'Hamlet'
                p.lastName = "D'Arcy"
                p.employer = 'Canoo'
                p.street = 'Kirschgaraten 5'
                p.city = 'Basel'

                def p1 = new Person().with {
                    firstName = 'Hamlet'
                    lastName = "D'Arcy"
                    employer = 'Canoo'
                    street = 'Kirschgaraten 5'
                    city = 'Basel'
                    zipCode = '4051'
                }

                def p2 = new Person().identity {
                    firstName = 'Hamlet'
                    lastName = "D'Arcy"
                    employer = 'Canoo'
                    street = 'Kirschgaraten 5'
                    city = 'Basel'
                    zipCode = '4051'
                } '''
        assertNoViolations(SOURCE)
    }

    void testExcessivePropertyAccess() {
        final SOURCE = '''
                class Person {
                    String firstName
                    String lastName
                    String employer
                    String street
                    String city
                    String zipCode
                }

                def p1 = new Person()
                p1.firstName = 'Hamlet'
                p1.lastName = "D'Arcy"
                p1.employer = 'Canoo'
                p1.street = 'Kirschgaraten 5'
                p1.city = 'Basel'
                p1.zipCode = '4051'

                def p2 = new Person()
                p2.firstName = 'Hamlet'
                p2.lastName = "D'Arcy"
                p2.employer = 'Canoo'
                p2.street = 'Kirschgaraten 5'
                p2.city = 'Basel'
                p2.zipCode = '4051'  '''
        assertTwoViolations(SOURCE, 17, "p1.zipCode = '4051'", 25, "p2.zipCode = '4051'")
    }

    void testOverridingProperty() {
        rule.maxReferencesAllowed = 2
        final SOURCE = '''
                class Person {
                    String firstName
                    String lastName
                    String employer
                    String street
                    String city
                    String zipCode
                }

                def p1 = new Person()
                p1.firstName = 'Hamlet'
                p1.lastName = "D'Arcy"
                p1.employer = 'Canoo'  '''
        assertSingleViolation(SOURCE, 14, "p1.employer = 'Canoo'", 'The code could be more concise by using a with() or identity() block')
    }

    void testExcessiveSetters() {
        final SOURCE = '''
                class Person {
                    String firstName
                    String lastName
                    String employer
                    String street
                    String city
                    String zipCode
                }

                def p2 = new Person()
                p2.setFirstName('Hamlet')
                p2.setLastName("D'Arcy")
                p2.setEmployer('Canoo')
                p2.setStreet('Kirschgaraten 5')
                p2.setCity('Basel')
                p2.setZipCode('4051') '''

        assertSingleViolation(SOURCE, 17, "p2.setZipCode('4051')",	'The code could be more concise by using a with() or identity() block')
    }

    protected Rule createRule() {
        new UnnecessaryObjectReferencesRule()
    }
}