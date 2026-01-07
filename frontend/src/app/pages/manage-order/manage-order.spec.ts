import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManageOrder } from './manage-order';

describe('ManageOrder', () => {
  let component: ManageOrder;
  let fixture: ComponentFixture<ManageOrder>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManageOrder]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ManageOrder);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
