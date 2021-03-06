/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.pools;

import java.util.ArrayList;
import java.util.List;

import com.dokkaebistudio.tacticaljourney.alterations.Blessing.BlessingsEnum;
import com.dokkaebistudio.tacticaljourney.alterations.Curse.CursesEnum;

/**
 * The alteration pool for the goddess statue.
 * @author Callil
 *
 */
public class GoddessStatueAlterationPool extends AlterationPool {
	
	/**
	 * This list contains the whole list of blessing that can be given by a goddess statue.
	 */
	private static final List<PooledAlterationDescriptor<BlessingsEnum>> blessingPool = new ArrayList<>();
	
	private static int blessingSumOfChances;
	
	static {
		blessingPool.add(new PooledAlterationDescriptor<BlessingsEnum>(BlessingsEnum.VIGOR, 10));
		blessingPool.add(new PooledAlterationDescriptor<BlessingsEnum>(BlessingsEnum.STRENGTH, 10));
		blessingPool.add(new PooledAlterationDescriptor<BlessingsEnum>(BlessingsEnum.CELERITY, 10));
		blessingPool.add(new PooledAlterationDescriptor<BlessingsEnum>(BlessingsEnum.ACCURACY, 10));
		blessingPool.add(new PooledAlterationDescriptor<BlessingsEnum>(BlessingsEnum.BOWMASTER_MIGHT, 10));
		blessingPool.add(new PooledAlterationDescriptor<BlessingsEnum>(BlessingsEnum.BOWMASTER_ACCURACY, 10));
		blessingPool.add(new PooledAlterationDescriptor<BlessingsEnum>(BlessingsEnum.BOWMASTER_STEADYNESS, 10));
		blessingPool.add(new PooledAlterationDescriptor<BlessingsEnum>(BlessingsEnum.BOMBMASTER_MIGHT, 10));
		blessingPool.add(new PooledAlterationDescriptor<BlessingsEnum>(BlessingsEnum.ROCK_THROWER, 10));
		blessingPool.add(new PooledAlterationDescriptor<BlessingsEnum>(BlessingsEnum.LUCK, 10));
						
		for (PooledAlterationDescriptor<BlessingsEnum> desc : blessingPool) {
			blessingSumOfChances += desc.getChanceToDrop();
		}
	}
	

	/**
	 * This list contains the whole list of blessing that can be given by a goddess statue.
	 */
	private static final List<PooledAlterationDescriptor<CursesEnum>> cursePool = new ArrayList<>();
	
	private static int curseSumOfChances;
	
	static {
		cursePool.add(new PooledAlterationDescriptor<CursesEnum>(CursesEnum.FRAILTY, 10));
		cursePool.add(new PooledAlterationDescriptor<CursesEnum>(CursesEnum.WEAKNESS, 10));
		cursePool.add(new PooledAlterationDescriptor<CursesEnum>(CursesEnum.SLOWNESS, 10));
		cursePool.add(new PooledAlterationDescriptor<CursesEnum>(CursesEnum.TREMORS, 10));
		cursePool.add(new PooledAlterationDescriptor<CursesEnum>(CursesEnum.BAD_LUCK, 10));
						
		for (PooledAlterationDescriptor<CursesEnum> desc : cursePool) {
			curseSumOfChances += desc.getChanceToDrop();
		}
	}

	@Override
	public List<PooledAlterationDescriptor<BlessingsEnum>> blessingPool() {
		return blessingPool;
	}


	@Override
	public int blessingSumOfChances() {
		return blessingSumOfChances;
	}


	@Override
	public List<PooledAlterationDescriptor<CursesEnum>> cursePool() {
		return cursePool;
	}


	@Override
	public int curseSumOfChances() {
		return curseSumOfChances;
	}

}
