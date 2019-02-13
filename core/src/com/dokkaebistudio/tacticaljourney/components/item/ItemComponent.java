package com.dokkaebistudio.tacticaljourney.components.item;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.TextComponent;
import com.dokkaebistudio.tacticaljourney.enums.items.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class ItemComponent implements Component, Poolable {
		
	/** The king of item. */
	private ItemEnum itemType;
	
	/** The random value used by some items. Null if not used. */
	private Integer quantity;
	
	/** The displayer that shows the quantity of this item (ex: quantity of arrows or bombs). */
	private Entity quantityDisplayer;
	
	/** The price of the item. Null if no price. */
	private Integer price;
	
	/** The displayer that shows the quantity of this item (ex: quantity of arrows or bombs). */
	private Entity priceDisplayer;
	
	
	//******************************
	// Animation related attributes
	
	private Integer quantityPickedUp;
	
	/** The sprite used for the drop animation. */
	private List<Image> pickupAnimationImages = new ArrayList<>();
	/** The sprite used for the drop animation. */
	private Image dropAnimationImage;





	@Override
	public void reset() {
		this.quantity = null;
		this.quantityPickedUp = null;
		this.quantityDisplayer = null;
		this.price = null;
		this.priceDisplayer = null;
		this.setDropAnimationImage(null);
		this.pickupAnimationImages.clear();
	}
	
	/**
	 * Pick up this item.
	 * @param picker the entity that picks it up
	 */
	public boolean pickUp(Entity picker, Entity item, Room room) {
		return itemType.pickUp(picker, item, room);
	}

	/**
	 * Use the given item.
	 * @param user the entity that uses the item (usually the player).
	 * @param item the item to use
	 */
	public boolean use(Entity user, Entity item, Room room) {
		return itemType.use(user, item, room);
	}
	
	/**
	 * Drop the given item.
	 * @param dropper the entity that drops the item (usually the player).
	 * @param item the item to drop
	 * @param room the room in which the item is dropped
	 * @return true if the item was dropped
	 */
	public boolean drop(Entity dropper, Entity item, Room room) {
		return itemType.drop(dropper, item, room);
	}
	
	
	/**
	 * Throw the item at the desired location.
	 * @param thrownPosition the location of the throw
	 * @param thrower the entity that threw this item
	 * @param item the item
	 * @param room the room
	 */
	public void onThrow(Vector2 thrownPosition, Entity thrower, Entity item, Room room) {
		itemType.onThrow( thrownPosition, thrower, item, room);
	}
	
	
	/**
	 * Set up the pickup animation.
	 * @param texture the texture to use
	 * @param pixelPos the tile on which the animation takes place
	 * @param dropAction the action to call after the movement is over
	 */
	public List<Image> getPickupAnimationImage(Entity item) {
		this.pickupAnimationImages.clear();
		
		GridPositionComponent itemPositionComponent = Mappers.gridPositionComponent.get(item);
		ItemComponent itemComponent = Mappers.itemComponent.get(item);
		
		
		int numberOfImages = 1;
		if (itemComponent.getQuantity() != null) {
			numberOfImages = itemComponent.getQuantityPickedUp() != null ? itemComponent.getQuantityPickedUp() : itemComponent.getQuantity();
		}
		
		for (int i=0 ; i<numberOfImages ; i++) {
		
			final Image pickupImage = new Image(Assets.getTexture(itemComponent.getItemType().getImageName()));
			
			Vector2 worldPos = itemPositionComponent.getWorldPos();
			pickupImage.setPosition(worldPos.x, worldPos.y);
			
			Action removeImageAction = new Action(){
			  @Override
			  public boolean act(float delta){
				  pickupImage.remove();
				  return true;
			  }
			};
	
			float duration = 0.1f * i;
			pickupImage.addAction(Actions.sequence(Actions.moveBy(0, 0, duration), Actions.moveTo(780, 30, 0.75f, Interpolation.circle),
					removeImageAction));
				
			this.pickupAnimationImages.add(pickupImage);
		}
		return this.pickupAnimationImages;
	}
	
	
	/**
	 * Set up the drop animation.
	 * @param texture the texture to use
	 * @param tilePos the tile on which the animation takes place
	 * @param dropAction the action to call after the movement is over
	 */
	public Image getDropAnimationImage(Entity dropper, Entity item, Action dropAction) {
		ItemComponent itemComponent = Mappers.itemComponent.get(item);
		
		final Image drop = new Image(Assets.getTexture(itemComponent.getItemType().getImageName()));
		
		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(dropper);
		Vector2 worldPos = gridPositionComponent.getWorldPos();
		drop.setPosition(worldPos.x, worldPos.y);
		
		Action removeImageAction = new Action(){
		  @Override
		  public boolean act(float delta){
			  drop.remove();
			  return true;
		  }
		};
		
		drop.addAction(Actions.sequence(Actions.moveBy(0, 50, 0.2f, Interpolation.pow3Out),
				Actions.moveBy(0, -50, 0.5f, Interpolation.bounceOut), 
				dropAction,
				removeImageAction));
			
		this.dropAnimationImage = drop;
		return drop;
	}
	
	
	//**********************************
	// Display util methods
	
	/**
	 * Get the label. Replace the # per the random number, and remove the [] depending on the singular or plural.
	 * @return the label to display
	 */
	public String getItemLabel() {
		if (this.getQuantity() != null) {
			String label = itemType.getLabel().replace("#", String.valueOf(this.getQuantity().intValue()));
			Integer val = this.getQuantity();
			if (val.intValue() == 1) {
				label = label.replaceAll("\\[.*?\\]", "");
			} else {
				label = label.replaceAll("\\[", "");
				label = label.replaceAll("\\]", "");
			}
			return label;
		}
		return itemType.getLabel();
	}
	
	public String getItemDescription() {
		return itemType.getDescription();
	}
	
	public String getItemActionLabel() {
		return itemType.getActionLabel();
	}
	
	public String getItemImageName() {
		return itemType.getImageName();
	}
	
	
	
	//*******************************
	// Getters and Setters

	public ItemEnum getItemType() {
		return itemType;
	}

	public void setItemType(ItemEnum itemType) {
		this.itemType = itemType;
	}

	public Integer getQuantity() {
		if (quantity == null && this.itemType.getRandomValueMax() != null) {
			RandomXS128 random = RandomSingleton.getInstance().getSeededRandom();
			int value = this.itemType.getRandomValueMin();
			if (this.itemType.getRandomValueMax() > this.itemType.getRandomValueMin()) {
				value += random.nextInt(this.itemType.getRandomValueMax() - this.itemType.getRandomValueMin());
			}
			setQuantity(value);
		}
		return quantity;
	}

	public void setQuantity(Integer value) {
		this.quantity = value;
		
		if (quantityDisplayer != null) {
			TextComponent textComponent = Mappers.textComponent.get(quantityDisplayer);
			textComponent.setText(String.valueOf(value));
		}
	}

	public Entity getQuantityDisplayer() {
		return quantityDisplayer;
	}

	public void setQuantityDisplayer(Entity quantityDisplayer) {
		this.quantityDisplayer = quantityDisplayer;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
		
		if (priceDisplayer != null) {
			if (price != null) {
				TextComponent textComponent = Mappers.textComponent.get(priceDisplayer);
				textComponent.setText(String.valueOf(price));
			} else {
				priceDisplayer = null;
			}
		}
	}

	public Entity getPriceDisplayer() {
		return priceDisplayer;
	}

	public void setPriceDisplayer(Entity priceDisplayer) {
		this.priceDisplayer = priceDisplayer;
	}

	public Image getDropAnimationImage() {
		return dropAnimationImage;
	}

	public void setDropAnimationImage(Image dropAnimationImage) {
		this.dropAnimationImage = dropAnimationImage;
	}

	public List<Image> getPickupAnimationImage() {
		return pickupAnimationImages;
	}

	public Integer getQuantityPickedUp() {
		return quantityPickedUp;
	}

	public void setQuantityPickedUp(Integer quantityPickedUp) {
		this.quantityPickedUp = quantityPickedUp;
	}
}
