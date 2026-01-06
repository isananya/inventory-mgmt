import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManageWarehouse } from './manage-warehouse';

describe('ManageWarehouse', () => {
  let component: ManageWarehouse;
  let fixture: ComponentFixture<ManageWarehouse>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManageWarehouse]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ManageWarehouse);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
