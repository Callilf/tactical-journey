package com.dokkaebistudio.tacticaljourney.ces.components.item;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.ces.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.interfaces.MarkerInterface;
import com.dokkaebistudio.tacticaljourney.ces.components.player.AlterationReceiverComponent;
import com.dokkaebistudio.tacticaljourney.ces.entity.PublicEntity;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.items.AbstractItem;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.AbstractInfusableItem;
import com.dokkaebistudio.tacticaljourney.rendering.service.PopinService;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class ItemComponent implements Component, Poolable, MarkerInterface {
		
	/** The king of item. */
	private AbstractItem itemType;
		
	/** The displayer that shows the quantity of this item (ex: quantity of arrows or bombs). */
	private Label quantityDisplayer = new Label("", PopinService.hudStyle());;
	
	/** The price of the item. Null if no price. */
	private Integer price;
	
	/** The displayer that shows the quantity of this item (ex: quantity of arrows or bombs). */
	private Label priceDisplayer = new Label("", PopinService.hudStyle());;
	
	
	//******************************
	// Animation related attributes
	
	private Integer quantityPickedUp;
	
	/** The sprite used for the drop animation. */
	private List<Image> pickupAnimationImages = new ArrayList<>();
	/** The sprite used for the drop animation. */
	private Image dropAnimationImage;

	
	
	@Override
	public void showMarker(Entity e) {
		if (quantityDisplayer != null) {
			GameScreen.fxStage.addActor(quantityDisplayer);
			this.place(Mappers.gridPositionComponent.get(e).coord());
		}
		if (priceDisplayer != null) {
			GameScreen.fxStage.addActor(priceDisplayer);
			this.place(Mappers.gridPositionComponent.get(e).coord());
		}

	}

	@Override
	public void hideMarker() {
		if (quantityDisplayer != null) {
			quantityDisplayer.remove();
		}
		if (priceDisplayer != null) {
			priceDisplayer.remove();
		}
	}



	@Override
	public void reset() {
		this.quantityPickedUp = null;
		this.price = null;
		if (quantityDisplayer != null) {
			quantityDisplayer.setText("");
			quantityDisplayer.remove();		
		}		
		if (priceDisplayer != null) {
			priceDisplayer.setText("");
			priceDisplayer.remove();		
		}	
		this.setDropAnimationImage(null);
		this.pickupAnimationImages.clear();
	}
	
	
	public void place(Vector2 tilePos) {
		if (quantityDisplayer != null) {
			quantityDisplayer.layout();
			Vector2 startPos = TileUtil.convertGridPosIntoPixelPos(tilePos);
			startPos.x += quantityDisplayer.getGlyphLayout().width/2;
			startPos.y += quantityDisplayer.getGlyphLayout().height/2;
			quantityDisplayer.setPosition(startPos.x, startPos.y);
		}
		if (priceDisplayer != null) {
			priceDisplayer.layout();
			Vector2 startPos = TileUtil.convertGridPosIntoPixelPos(tilePos);
			startPos.x += priceDisplayer.getGlyphLayout().width/2;
			startPos.y = startPos.y + GameScreen.GRID_SIZE - priceDisplayer.getGlyphLayout().height;
			priceDisplayer.setPosition(startPos.x, startPos.y);
		}
	}
	
	
	/**
	 * Pick up this item.
	 * @param picker the entity that picks it up
	 */
	public boolean pickUp(Entity picker, Entity item, Room room) {
		AlterationReceiverComponent alterationReceiverComponent = Mappers.alterationReceiverComponent.get(picker);
		if (alterationReceiverComponent != null) {
			boolean canPickUp = alterationReceiverComponent.onPickupItem(picker, item, room) >= 0;
			if (!canPickUp) return false;
		}
		
		return itemType.pickUp(picker, item, room);
	}

	/**
	 * Use the given item.
	 * @param user the entity that uses the item (usually the player).
	 * @param item the item to use
	 */
	public boolean use(Entity user, Entity item, Room room) {
		AlterationReceiverComponent alterationReceiverComponent = Mappers.alterationReceiverComponent.get(user);
		if (alterationReceiverComponent != null) {
			boolean canUse = alterationReceiverComponent.onUseItem(user, item, room) >= 0;
			if (!canUse) return false;
		}
		
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
	 * Drop the given item.
	 * @param position the position where the item is dropped.
	 * @param item the item to drop
	 * @param room the room in which the item is dropped
	 * @return true if the item was dropped
	 */
	public boolean drop(Vector2 position, Entity item, Room room) {
		return itemType.drop(position, item, room);
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
	 * Infuse the given item.
	 * @param player the player.
	 * @param item the item to infuse
	 * @param room the room
	 * @return true if the item was infused correctly
	 */
	public boolean infuse(Entity player, Entity item, Room room) {
		return itemType.infuse(player, item, room);
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
		
		Vector2 moveDest = itemComponent.getItemType().getPickupImageMoveDestination();
		if (moveDest == null) return this.pickupAnimationImages;
		
		int numberOfImages = 1;
		if (itemComponent.getQuantity() != null) {
			numberOfImages = itemComponent.getQuantityPickedUp() != null ? itemComponent.getQuantityPickedUp() : itemComponent.getQuantity();
		}
		
		for (int i=0 ; i<numberOfImages ; i++) {
		
			final Image pickupImage = new Image(itemComponent.getItemType().getTexture().getRegion());
			
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
			pickupImage.addAction(Actions.sequence(Actions.moveBy(0, 0, duration), Actions.moveTo(moveDest.x, moveDest.y, 0.75f, Interpolation.circle),
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
	public Image getDropAnimationImage(Entity dropper, Entity item, Action dropAction, float delay) {
		ItemComponent itemComponent = Mappers.itemComponent.get(item);
		
		final Image drop = new Image(itemComponent.getItemType().getTexture().getRegion());
		
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
		
		SequenceAction initAction = Actions.sequence(Actions.alpha(0f), 
				Actions.delay(delay),
				Actions.alpha(1f));
		
		drop.addAction(Actions.sequence(initAction, Actions.moveBy(0, 50, 0.2f, Interpolation.pow3Out),
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
	
	public Integer getItemRecyclePrice() {
		return itemType.getRecyclePrice();
	}
	
	public RegionDescriptor getItemImageName() {
		return itemType.getTexture();
	}
	
	
	public boolean isInfusable() {
		return itemType instanceof AbstractInfusableItem;
	}
	
	//*******************************
	// Getters and Setters

	public AbstractItem getItemType() {
		return itemType;
	}

	public void setItemType(AbstractItem itemType, PublicEntity itemEntity) {
		this.itemType = itemType;
		if (this.itemType instanceof AbstractInfusableItem) {
			AbstractInfusableItem infusableItem = (AbstractInfusableItem) this.itemType;
			infusableItem.setItemEntity(itemEntity);
		}
		
		if (this.itemType.getQuantity() != null && quantityDisplayer != null) {
			quantityDisplayer.setText(String.valueOf(this.itemType.getQuantity()));
		}
	}

	public Integer getQuantity() {
		return this.itemType.getQuantity();
	}

	public void setQuantity(Integer value) {
		this.itemType.setQuantity(value);
		
		if (quantityDisplayer != null) {
			if (value != null) {
				quantityDisplayer.setText(String.valueOf(value));
			} else {
				quantityDisplayer.setText("");
			}
		}
	}

	public Label getQuantityDisplayer() {
		return quantityDisplayer;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
		
		if (price != null && this.getQuantity() != null) {
			this.price = this.price * this.getQuantity();
		}
		
		if (priceDisplayer != null) {
			if (price != null) {
				priceDisplayer.setText("[GOLD]" + String.valueOf(price));
			} else {
				priceDisplayer.setText("");
			}
		}
		
	}

	public Label getPriceDisplayer() {
		return priceDisplayer;
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
	
	
	
	
	
	
	public static Serializer<ItemComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<ItemComponent>() {

			@Override
			public void write(Kryo kryo, Output output, ItemComponent object) {
				kryo.writeClassAndObject(output, object.itemType);
				kryo.writeClassAndObject(output, object.price);
			}

			@Override
			public ItemComponent read(Kryo kryo, Input input, Class<? extends ItemComponent> type) {
				ItemComponent compo = engine.createComponent(ItemComponent.class);
				compo.itemType = (AbstractItem) kryo.readClassAndObject(input);
				compo.setPrice((Integer) kryo.readClassAndObject(input));
				compo.setQuantity(compo.itemType.getQuantity());
				
				return compo;
			}
		
		};
	}

}
