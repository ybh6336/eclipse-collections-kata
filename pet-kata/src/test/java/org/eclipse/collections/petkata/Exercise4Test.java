/*
 * Copyright (c) 2020 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.petkata;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.collections.api.bag.ImmutableBag;
import org.eclipse.collections.api.set.primitive.IntSet;
import org.eclipse.collections.impl.block.factory.primitive.IntPredicates;
import org.eclipse.collections.impl.factory.primitive.IntSets;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;
import org.junit.Assert;
import org.junit.Test;

import static java.lang.Math.abs;

/**
 * In this set of tests, wherever you see .stream() replace it with an Eclipse Collections alternative.
 * <p/>
 * {@link org.eclipse.collections.api.list.primitive.MutableIntList}<br>
 * {@link org.eclipse.collections.api.set.primitive.IntSet}<br>
 * {@link org.eclipse.collections.impl.factory.primitive.IntSets}<br>
 * {@link org.eclipse.collections.impl.block.factory.primitive.IntPredicates}<br>
 * {@link org.eclipse.collections.api.bag.MutableBag}<br>
 * {@link org.eclipse.collections.api.list.MutableList}<br>
 *
 * @see <a href="http://eclipse.github.io/eclipse-collections-kata/pet-kata/#/8">Exercise 4 Slides</a>
 */
public class Exercise4Test extends PetDomainForKata
{
    @Test
    public void getAgeStatisticsOfPets()
    {
//        Assert.fail("Refactor to Eclipse Collections. Don't forget to comment this out or delete it when you are done.");

        // Try to use a MutableIntList here instead
        // Hints: flatMap = flatCollect, map = collect, mapToInt = collectInt
//        var petAges = this.people
//                .stream()
//                .map(Person::getPets)
//                .flatMap(List::stream)
//                .mapToInt(Pet::getAge)
//                .boxed()
//                .collect(Collectors.toList());

        var petAges = this.people
                .flatCollect(Person::getPets)
                .collectInt(Pet::getAge);

        // Try to use an IntSet here instead
//        var uniqueAges = Set.copyOf(petAges);
        var uniqueAges = petAges.toSet();

        // IntSummaryStatistics is a class in JDK 8 - Look at MutableIntList.summaryStatistics().
//        var stats = petAges.stream().mapToInt(Integer::intValue).summaryStatistics();
        var stats = petAges.summaryStatistics();

        // Is a Set<Integer> equal to an IntSet?
        // Hint: Try IntSets instead of Set as the factory
//        var expectedSet = Set.of(1, 2, 3, 4);
        var expectedSet = IntSets.immutable.of(1, 2, 3, 4);
        Assert.assertEquals(expectedSet, uniqueAges);

        // Try to leverage minIfEmpty, maxIfEmpty, sum, averageIfEmpty on IntList
        Assert.assertEquals(stats.getMin(), petAges.minIfEmpty(0));
        Assert.assertEquals(stats.getMax(), petAges.maxIfEmpty(0));
        Assert.assertEquals(stats.getSum(), petAges.sum());
        Assert.assertEquals(stats.getAverage(), petAges.averageIfEmpty(0.0), 0.0);
        Assert.assertEquals(stats.getCount(), petAges.size());

        // Hint: JDK xyzMatch = Eclipse Collections xyzSatisfy
        // Use IntPredicates, lambda or both?
        Assert.assertTrue(petAges.allSatisfy(i -> i > 0));
        Assert.assertTrue(petAges.allSatisfy(IntPredicates.greaterThan(0)));
        Assert.assertFalse(petAges.anySatisfy(i -> i == 0));
        Assert.assertTrue(petAges.noneSatisfy(i -> i < 0));
    }

    @Test
    public void streamsToECRefactor1()
    {
//        Assert.fail("Refactor to Eclipse Collections. Don't forget to comment this out or delete it when you are done.");

        //find Bob Smith
//        Person person = this.people
//                .stream()
//                .filter(each -> each.named("Bob Smith"))
//                .findFirst()
//                .orElse(null);

        Person person = this.people
                .detectWith(Person::named, "Bob Smith");

        //get Bob Smith's pets' names
//        String names = person.getPets()
//                .stream()
//                .map(Pet::getName)
//                .collect(Collectors.joining(" & "));

        String names = person.getPets().collect(Pet::getName).makeString(" & ");

        Assert.assertEquals("Dolly & Spot", names);
    }

    @Test
    public void streamsToECRefactor2()
    {
//        Assert.fail("Refactor to Eclipse Collections. Don't forget to comment this out or delete it when you are done.");

        // Hint: Try to replace the immutable Map<PetType, Long> with an ImmutableBag<PetType>
//        Map<PetType, Long> counts =
//                Map.copyOf(this.people
//                        .stream()
//                        .flatMap(person -> person.getPets().stream())
//                        .collect(Collectors.groupingBy(Pet::getType, Collectors.counting())));

        ImmutableBag<PetType> counts = this.people
                .flatCollect(Person::getPets)
                .countBy(Pet::getType)
                .toImmutable();

        Assert.assertEquals(2, counts.occurrencesOf(PetType.CAT));
        Assert.assertEquals(2, counts.occurrencesOf(PetType.DOG));
        Assert.assertEquals(2, counts.occurrencesOf(PetType.HAMSTER));
        Assert.assertEquals(1, counts.occurrencesOf(PetType.SNAKE));
        Assert.assertEquals(1, counts.occurrencesOf(PetType.TURTLE));
        Assert.assertEquals(1, counts.occurrencesOf(PetType.BIRD));
    }

    /**
     * The purpose of this test is to determine the top 3 pet types.
     */
    @Test
    public void streamsToECRefactor3()
    {
//        Assert.fail("Refactor to Eclipse Collections. Don't forget to comment this out or delete it when you are done.");

        // Hint: The result of groupingBy/counting can almost always be replaced by a Bag
        // Hint: Look for the API on Bag that might return the top 3 pet types
//        var favorites = this.people
//                .stream()
//                .flatMap(p -> p.getPets().stream())
//                .collect(Collectors.groupingBy(Pet::getType, Collectors.counting()))
//                .entrySet()
//                .stream()
//                .sorted(Comparator.comparingLong(e -> -e.getValue()))
//                .limit(3L)
//                .collect(Collectors.toList());

        var favorites = this.people
                .flatCollect(Person::getPets)
                .countBy(Pet::getType)
                .topOccurrences(3);

        Verify.assertSize(3, favorites);

        // Hint: Look at PrimitiveTuples.pair(Object, int)
        Verify.assertContains(PrimitiveTuples.pair(PetType.CAT, 2), favorites);
        Verify.assertContains(PrimitiveTuples.pair(PetType.DOG, 2), favorites);
        Verify.assertContains(PrimitiveTuples.pair(PetType.HAMSTER, 2), favorites);
    }

    @Test
    public void getMedianOfPetAges()
    {
//        Assert.fail("Refactor to Eclipse Collections. Don't forget to comment this out or delete it when you are done.");

        // Try to use a MutableIntList here instead
        // Hints: flatMap = flatCollect, map = collect, mapToInt = collectInt
//        var petAges = this.people
//                .stream()
//                .map(Person::getPets)
//                .flatMap(List::stream)
//                .mapToInt(Pet::getAge)
//                .boxed()
//                .collect(Collectors.toList());

        var petAges = this.people
                .flatCollect(Person::getPets)
                .collectInt(Pet::getAge);

        // Try to refactor the code block finding the median the JDK way
        // Use the EC median method
//        var sortedPetAges = petAges.stream().sorted().collect(Collectors.toList());
//
//        double median;
//        if (0 == sortedPetAges.size() % 2)
//        {
//            // The median of a list of even numbers is the average of the two middle items
//            median = sortedPetAges.stream().skip((sortedPetAges.size() / 2) - 1).limit(2L).mapToInt(i -> i).average().getAsDouble();
//        }
//        else
//        {
//            // The median of a list of odd numbers is the middle item
//            median = sortedPetAges.get(abs(sortedPetAges.size() / 2)).doubleValue();
//        }

        Assert.assertEquals(2.0, petAges.median(), 0.0);
    }
}
