package com.dokkaebistudio.tacticaljourney.alterations.pools;

public class PooledAlterationDescriptor<S> {

	private S type;
	private int chanceToDrop;
	
	
	public PooledAlterationDescriptor(S type, int chanceToDrop) {
		this.setType(type);
		this.setChanceToDrop(chanceToDrop);
	}

	
	public S getType() {
		return type;
	}

	public void setType(S type) {
		this.type = type;
	}

	public int getChanceToDrop() {
		return chanceToDrop;
	}

	public void setChanceToDrop(int chanceToDrop) {
		this.chanceToDrop = chanceToDrop;
	}
	
	
	
}
